package com.mezon.classmanagement.backend_document.domain.component.ingest.service;

import com.mezon.classmanagement.backend_document.domain.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByParagraphStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class IngestService {

	ChunkService chunkService;

	SplitByParagraphStrategy splitByParagraphStrategy;

	@Async
	public void ingest(
			MultipartFile file
	) throws Exception {
		File tmp = File.createTempFile("upload-", ".tmp");
		try {
			file.transferTo(tmp);

			List<String> chunkList = chunkService.getChunkListFromFile(
					tmp,
					splitByParagraphStrategy
			);

			chunkList.forEach(System.out::println);
		} finally {
			if (tmp.exists()) {
				Files.delete(Paths.get(tmp.getAbsolutePath()));
			}
		}
	}
}