package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.ChunkFormatterPipeline;
import com.mezon.classmanagement.backend.domain_document.component.chunk.formatter.impl.GeminiChunkFormatter;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.tag.service.TagService;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class VnCoreNLPTest {

	TagService tagService;
	ChunkService chunkService;
	ChunkStrategy chunkByParagraphWithoutOverlapStrategy;

	@Test
	public void wsegTest() throws Exception {
		String fileName = "triethocmaclenin.docx";

		ClassPathResource classPathResource = new ClassPathResource(fileName);
		MockMultipartFile multipartFile = new MockMultipartFile(fileName, classPathResource.getInputStream());

		DocumentSource documentSource = new MultipartFileDocumentSource(multipartFile);

		List<String> chunkList = chunkService
				.getChunkList(
						chunkByParagraphWithoutOverlapStrategy,
						documentSource
				);

		ChunkFormatter chunkFormatter = new ChunkFormatterPipeline(
				List.of(
						//new GeminiChunkFormatter()
				)
		);
		Metadata metadata = documentSource.toDocument().metadata();
		List<String> formattedChunkList = chunkFormatter.formatList(
				chunkList,
				metadata.put("fileName", multipartFile.getName())
		);

		List<String> tagList = tagService.extractTagsWithVnCoreNLP(formattedChunkList, 20);

		System.out.println("==========\n");
		tagList.forEach(System.out::println);
		System.out.println("\n==========");

//		List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.getContent(fileName), 20);
//
//		System.out.println("==========\n");
//		tagList.forEach(System.out::println);
//		System.out.println("\n==========");

//		System.out.println("==========\n");
//		formattedChunkList.forEach(System.out::println);
//		System.out.println("\n==========");
	}

//	@Test
//	public void wsegTest2() throws Exception {
//		String fileName = "triethocmaclenin.docx";
//
//		List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.getContent(fileName), 20);
//
//		System.out.println("==========\n");
//		tagList.forEach(System.out::println);
//		System.out.println("\n==========");
//	}

}