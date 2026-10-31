package com.mezon.classmanagement.backend.domain_document.component.ingest.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatterPipeline;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.impl.GeminiChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import com.mezon.classmanagement.backend.domain_document.component.document_chunk.service.DocumentChunkService;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.InputStreamDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.ingest.dto.IngestRequest;
import com.mezon.classmanagement.backend.domain_document.component.ingest.dto.RabbitIngestRequest;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.directory.service.DirectoryService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.InternalUpdateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.mapper.DocumentMapper;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.rabbitmq.FileProducer;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class IngestService {

	EmbeddingService embeddingService;
	DirectoryService directoryService;
	DocumentService documentService;
	DocumentChunkService documentChunkService;
	DocumentMapper documentMapper;
	S3Service s3Service;
	ChunkService chunkService;
	ChunkByParagraphWithoutOverlapStrategy chunkByParagraphWithoutOverlapStrategy;
	ChunkByParagraphWithOverlapStrategy chunkByParagraphWithOverlapStrategy;
	FileProducer fileProducer;
//	TagService tagService;

//	DocumentMetadataService documentMetadataService;


//	private static final Logger log = LoggerFactory.getLogger(IngestService.class);

	Queue<IngestRequest> ingestRequestQueue = new ArrayDeque<>();

	private record PreparedData(
			IngestRequest ingestRequest,
			Path tempPath,
			Metadata metadata
	) {}

	public void ingestMultiple(List<IngestRequest> ingestRequestList) {
		List<String> failedFileList = new ArrayList<>();
		List<PreparedData> dataList = new ArrayList<>();

		Iterator<IngestRequest> ingestRequestIterator = ingestRequestList.iterator();
		while (ingestRequestIterator.hasNext()) {
			IngestRequest request = ingestRequestIterator.next();
			MultipartFile multipartFile = request.multipartFile();

			Path tempPath;
			try {
				tempPath = Files.createTempFile("document-", ".tmp");
				multipartFile.transferTo(tempPath);

				dataList.add(
						new PreparedData(
								request,
								tempPath,
								new Metadata()
										.put("fileName", multipartFile.getOriginalFilename())
										.put("fileExtension", FileUtils.getExtension(multipartFile))
										.put("fileMimeType", FileUtils.detectMimeTypeFromMultipartFile(multipartFile))
										.put("fileSize", multipartFile.getSize())
						)
				);
			} catch (Exception e) {
				System.out.println(
						"Không thể tạo file tạm: "
								+ request.multipartFile().getOriginalFilename()
				);
				System.out.println(e.getMessage());

				failedFileList.add(request.multipartFile().getOriginalFilename());
				ingestRequestIterator.remove();
			}
		}

		for (PreparedData data : dataList) {
			IngestRequest request = data.ingestRequest();

			ingestAsync(
					request.classId(),
					request.directoryId(),
					request.creatorUserId(),
					data.tempPath(),
					data.metadata()
			);
		}
	}

	@Deprecated(forRemoval = true)
	@Transactional
	public void ingestSingle(
			Long creatorUserId,
			Long classId,
			Long directoryId
	) {


//		try {
//			Map<String, Object> metadataMap = documentMetadataService.extract(multipartFile);
//			documentMetadataService.saveMetadata(document.getId(), metadataMap);
//		} catch (Exception e) {
//			log.warn("Extract metadata failed, documentId={}", document.getId(), e);
//		}


		// 4. Tiếp theo: xử lý bất đồng bộ
//		ingestAsync(
//				classId,
//				directory,
//				document,
//				tempFile.toString(),
//				metadata
//		);

	}

	@Async("documentExecutor")
	protected void ingestAsync(
			Long classId,
			Long directoryId,
			Long creatorUserId,
			Path tempFile,
			Metadata metadata
	) {
		Directory directory = directoryService.findByIdOrThrow(directoryId);

		Document document;
		try {
			document = documentService.create(
					creatorUserId, directoryId, metadata
			);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}

		try (InputStream inputStream = Files.newInputStream(tempFile)) {
			String uploadedFilePath = s3Service.uploadFileToClass(
					classId,
					directory,
					document,
					inputStream,
					metadata
			);

			fileProducer.send(
					new RabbitIngestRequest(
							classId,
							document.getId(),
							uploadedFilePath,
							metadata.getString("fileName")
					)
			);

//			ingestFromRabbit(
//					new RabbitIngestRequest(
//							classId,
//							document.getId(),
//							uploadedFilePath,
//							metadata.getString("fileName")
//					)
//			);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage()
			);
		}
	}

	@Transactional
	public void ingestFromRabbit(
			RabbitIngestRequest request
	) {
		System.out.println(
				"Start ingest: " + request.originalFileName()
		);

		try (InputStream inputStream = s3Service.getFile(request.filePathInS3())) {
			DocumentSource documentSource = new InputStreamDocumentSource(inputStream);

			List<String> chunkList = chunkService
					.getChunkList(
							chunkByParagraphWithOverlapStrategy,
							documentSource
					);

//			try {
//				tagService.autoTag(request.documentId(), chunkList);
//			} catch (Exception e) {
//				log.warn("Auto-tag failed (khong chan ingest), documentId={}", request.documentId(), e);
//			}

			List<DocumentChunk> documentChunkList = new ArrayList<>();
			List<BaseVector> embeddingList = embeddingService
					.embedMultipleDocumentText(
							request.originalFileName(),
							new ChunkFormatterPipeline(
									List.of(
											new GeminiChunkFormatter()
									)
							).formatList(
									chunkList,
									new Metadata().put("fileName", request.originalFileName())
							)
					);
			for (short i = 0; i < chunkList.size(); i++) {
				documentChunkList.add(
						DocumentChunk
								.builder()
								.document(Document.create(request.documentId()))
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

			String documentHeader;
			if (documentChunkList.size() >= 2) {
				documentHeader = documentChunkList
						.get(0).getContent() + " " + documentChunkList.get(1).getContent();
			} else {
				documentHeader = documentChunkList.getFirst().getContent();
			}
			Vector1536 embedding = new Vector1536(
					embeddingService
							.embedSingleDocumentText(
									request.originalFileName(),
									documentHeader
							)
							.getValues());
			documentService.updateInternal(
					request.documentId(),
					new InternalUpdateDocumentRequestDto(
							Document.Status.UPLOADED,
							embedding
					)
			);

			System.out.println("Ingest completed: " + request.originalFileName());
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage() + e.getLocalizedMessage()
			);
		}

//		try {
//			s3Service.deleteTempFile(FileUtils.getName(request.filePathInS3()));
//		} catch (Exception e) {
//			throw new GlobalException(
//					GlobalException.Type.INTERNAL_SERVER_ERROR
//			);
//		}
	}

}