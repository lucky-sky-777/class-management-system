package com.mezon.classmanagement.backend.domain_document.component.ingest.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.service.DocumentChunkService;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.InputStreamDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.directory.service.DirectoryService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.InternalUpdateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.rabbitmq.FileProducer;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class IngestService {

	EmbeddingService embeddingService;
	DirectoryService directoryService;
	DocumentService documentService;
	DocumentChunkService documentChunkService;
	S3Service s3Service;
	ChunkService chunkService;
	ChunkByParagraphWithoutOverlapStrategy chunkByParagraphWithoutOverlapStrategy;
	ChunkByParagraphWithOverlapStrategy chunkByParagraphWithOverlapStrategy;
	FileProducer fileProducer;

	@Async("documentExecutor")
	public void ingest(
			Long classId,
			Long directoryId,
			Long documentId,
			Path tempFile,
			Metadata metadata,
			String documentKey
	) {
		try (InputStream inputStream = Files.newInputStream(tempFile)) {
			Directory directory = directoryService.findByIdOrThrow(directoryId);

			String uploadedFilePath = s3Service.uploadFileToPath(
					inputStream,
					metadata,
					S3Config.Bucket.ROOT,
					FileUtils.buildPath(
							S3Config.Directory.UPLOAD_CLASS,
							String.valueOf(classId),
							directory.getSlug()
					),
					documentKey + "." + metadata.getString("fileExtension")
			);

			fileProducer.send(
					classId,
					documentId,
					uploadedFilePath,
					metadata.getString("fileName")
			);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}
	}

	public void ingest(
			Long classId,
			Long documentId,
			String filePath,
			String originalFileName
	) {
		System.out.println(
				"Start ingest: " + filePath
		);

		try (InputStream inputStream = s3Service.getFile(filePath)) {
			DocumentSource documentSource = new InputStreamDocumentSource(inputStream);

			List<String> chunkList = chunkService
					.getChunkList(
							chunkByParagraphWithOverlapStrategy,
							documentSource
					);

			List<DocumentChunk> documentChunkList = new ArrayList<>();
			List<BaseVector> embeddingList = embeddingService
					.embedMultipleDocumentText(
							originalFileName,
							chunkList
					);
			for (short i = 0; i < chunkList.size(); i++) {
				documentChunkList.add(
						DocumentChunk
								.builder()
								.document(Document.create(documentId))
								.index(i)
								.content(chunkList.get(i))
								.embedding(
										new Vector1536(
												embeddingList.get(i).getValues()
										)
								)
								.build()
				);
			}

			documentChunkService.insertBatch(documentChunkList);

			documentService.updateInternal(
					documentId,
					new InternalUpdateDocumentRequestDto(
							Document.Status.UPLOADED
					)
			);

//			List<String> formattedChunkList = new ChunkFormatterPipeline(
//					List.of(
//							new GeminiChunkFormatter()
//					)
//			).formatList(
//					chunkList,
//					new Metadata().put("fileName", originalFileName)
//			);


			System.out.println("Ingest completed");
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage() + e.getLocalizedMessage()
			);
		}

		try {
			s3Service.deleteTempFile(FileUtils.getName(filePath));
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR
			);
		}
	}

}