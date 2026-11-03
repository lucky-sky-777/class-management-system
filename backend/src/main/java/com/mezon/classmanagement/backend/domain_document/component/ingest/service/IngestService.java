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
import com.mezon.classmanagement.backend.domain_document.component.document_metadata.service.DocumentMetadataService;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service.TagService;
import com.mezon.classmanagement.backend.domain_document.rabbitmq.FileProducer;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

	DocumentMetadataService documentMetadataService;
	TagService tagService;
	Logger log = LoggerFactory.getLogger(IngestService.class);

	Queue<IngestRequest> ingestRequestQueue = new ArrayDeque<>();

	public record PreparedData(
			IngestRequest ingestRequest,
			Path tempPath,
			Metadata metadata,
			Map<String, Object> metadataMap
	) {
	}

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
										.put("fileSize", multipartFile.getSize()),
								documentMetadataService.extract(multipartFile)
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

		ingestAsync(dataList);
	}

	@Async("documentExecutor")
	protected void ingestAsync(List<PreparedData> preparedDataList) {
		for (PreparedData data : preparedDataList) {
			IngestRequest request = data.ingestRequest();

			ingestAsync(
					request.classId(),
					request.directoryId(),
					request.creatorUserId(),
					data.tempPath(),
					data.metadata(),
					data.metadataMap()
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
			Metadata metadata,
			Map<String, Object> metadataMap

	) {
		// 1. Tìm thư mục
		Directory directory = directoryService.findByIdOrThrow(directoryId);

		// 2. Tạo document ở database
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

		try {
			documentMetadataService.saveMetadata(document.getId(), metadataMap);
		} catch (Exception e) {
			log.warn("Extract metadata failed, documentId={}", document.getId(), e);
		}

		// 3. Tải file lên cloud
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
		} catch (Exception e) {
			tryDeleteDocument(document.getId());
		}
	}

	public void ingestFromRabbit(
			RabbitIngestRequest request
	) {
		System.out.println(
				"Start ingest: " + request.originalFileName()
		);

		// 1. Lấy file từ cloud
		try (InputStream inputStream = s3Service.getFile(request.filePathInS3())) {

			// 2. Tạo danh sách chunk
			DocumentSource documentSource = new InputStreamDocumentSource(inputStream);

			List<String> chunkList;
			try {
				chunkList = chunkService
						.getChunkList(
								chunkByParagraphWithOverlapStrategy,
								documentSource
						);
			} catch (Exception e) {
				tryDeleteDocument(request.documentId());
				return;
			}

				try {
						tagService.autoTag(request.documentId(), chunkList);
				} catch (Exception e) {
					log.warn("Auto-tag failed (khong chan ingest), documentId={}",
							request.documentId(), e);
				}

				// 3. Tạo danh sách vector từ danh sách chunk
			List<BaseVector> embeddingList;
			try {
				embeddingList = embeddingService
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
			} catch (Exception e) {
				tryDeleteDocument(request.documentId());
				return;
			}

			// 5. Tạo và lưu danh sách DocumentChunk vào database
			List<DocumentChunk> documentChunkList;
			try {
				documentChunkList = new ArrayList<>();
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
			} catch (Exception out) {
				tryDeleteDocument(request.documentId());
				return;
			}

			// 6. Lấy tối đa 2 chunk đầu tiên từ danh sách chunk
			// và tạo vector đại diện cho tài liệu từ 2 chunk đó
			String documentHeader;
			try {
				if (chunkList.size() >= 2) {
					documentHeader = chunkList
							.get(0) + " " + chunkList.get(1);
				} else {
					documentHeader = chunkList.getFirst();
				}
			} catch (Exception e) {
				tryDeleteDocument(request.documentId());
				return;
			}

			Vector1536 embedding;
			try {
				embedding = new Vector1536(
						embeddingService
								.embedSingleDocumentText(
										request.originalFileName(),
										documentHeader
								)
								.getValues());
			} catch (Exception e) {
				tryDeleteDocument(request.documentId());
				return;
			}

			// 7. Đánh dấu document là đã upload
			try {
				documentService.updateInternal(
						request.documentId(),
						new InternalUpdateDocumentRequestDto(
								Document.Status.UPLOADED,
								embedding
						)
				);
			} catch (Exception e) {
				tryDeleteDocument(request.documentId());
				return;
			}

			System.out.println("Ingest completed: " + request.originalFileName());
		} catch (Exception e) {
			try {
				documentService.updateInternal(
						request.documentId(),
						new InternalUpdateDocumentRequestDto(
								Document.Status.ERROR,
								null
						)
				);
			} catch (Exception e1) {
				e.addSuppressed(e1);
				return;
			}

			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					e.getMessage() + e.getLocalizedMessage()
			);
		}

	}

	public void tryDeleteDocument(Long documentId) {
		try {
			documentService.delete(Document.create(documentId));
		} catch (Exception out) {
			try {
				documentService.updateInternal(
						documentId,
						new InternalUpdateDocumentRequestDto(
								Document.Status.ERROR,
								null
						)
				);
			} catch (Exception e) {
				throw new GlobalException(
						GlobalException.Type.INTERNAL_SERVER_ERROR,
						e.getMessage()
				);
			}
		}
	}

}