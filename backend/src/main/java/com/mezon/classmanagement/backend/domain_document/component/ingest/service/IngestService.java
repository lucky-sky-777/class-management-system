package com.mezon.classmanagement.backend.domain_document.component.ingest.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatterPipeline;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.impl.GeminiChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.FileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.InputStreamDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service.TagService;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.rabbitmq.FileProducer;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class IngestService {

	DocumentService documentService;
	S3Service s3Service;
	ChunkService chunkService;
	ChunkByParagraphWithoutOverlapStrategy chunkByParagraphWithoutOverlapStrategy;
	ChunkByParagraphWithOverlapStrategy chunkByParagraphWithOverlapStrategy;
	FileProducer fileProducer;
	TagService tagService;

	public void ingest(MultipartFile file) {
		try {
			String tmp = FileUtils.createS3TempFile(s3Service, file);

			fileProducer.send(
					tmp,
					file.getOriginalFilename()
			);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR
			);
		}
	}

	public void ingest(
			String filePath,
			String originalFileName
	) {
		System.out.println(
				"Start ingest: " + filePath
		);

		try (InputStream inputStream = s3Service.getTempFile(FileUtils.getName(filePath))) {
			DocumentSource documentSource = new InputStreamDocumentSource(inputStream);

			List<String> chunkList = chunkService
					.getChunkList(
							chunkByParagraphWithOverlapStrategy,
							documentSource
					);

			try {
				tagService.autoTag(originalFileName, chunkList);
			} catch (Exception e) {
				System.out.println("Auto-tag failed (khong chan ingest): " + e.getMessage());
			}



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