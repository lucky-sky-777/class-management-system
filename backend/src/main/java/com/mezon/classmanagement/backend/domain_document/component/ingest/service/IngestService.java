package com.mezon.classmanagement.backend.domain_document.component.ingest.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.rabbitmq.FileProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class IngestService {

	ChunkService chunkService;

	SplitByParagraphStrategy splitByParagraphStrategy;
	FileProducer fileProducer;

	public void ingest(MultipartFile file) {
		try {
			Path tmp = FileUtils.createTempFile(file);

			fileProducer.send(
					tmp.toAbsolutePath().toString()
			);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR
			);
		}
	}

	public void ingest(
			String filePath
	) {
		Path path = Paths.get(filePath);
		System.out.println(
				"Start ingest: " + path.toAbsolutePath()
		);

		List<String> chunkList =
				chunkService.getChunkListFromFile(
						path.toFile(),
						splitByParagraphStrategy
				);

		chunkList.forEach(System.out::println);

		System.out.println("Ingest completed");

		try {
			Files.deleteIfExists(path);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR
			);
		}
	}

}