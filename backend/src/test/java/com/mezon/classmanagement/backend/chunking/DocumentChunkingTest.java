package com.mezon.classmanagement.backend.chunking;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.SplitByAllStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.SplitByLineStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.SplitBySentenceStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.SplitByWordStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class DocumentChunkingTest {

	ChunkService chunkService;

	SplitByAllStrategy splitByAllStrategy;
	SplitByParagraphStrategy splitByParagraphStrategy;
	SplitByParagraphWithoutOverlapStrategy splitByParagraphWithoutOverlapStrategy;
	SplitByLineStrategy splitByLineStrategy;
	SplitBySentenceStrategy splitBySentenceStrategy;
	SplitByWordStrategy splitByWordStrategy;

//	@Test
//	public void testToken() {
//		List<String> list = chunkService.getChunkListFromFilePath(
//				"src/main/resources/TTNT.docx",
//				splitByAllStrategy
//		);
//
//		System.out.println("token");
//		list.forEach(System.out::println);
//	}

//	@Test
//	public void testParagraph() {
//		List<String> list = chunkService.getChunkListFromFilePath(
//				"src/main/resources/triethocmaclenin.docx",
//				splitByParagraphStrategy
//		);
//		System.out.println("begin with overlap");
//		list.forEach(System.out::println);
//		System.out.println("end with overlap");
//	}

	/**
	 * Nên dùng cái này
	 */
	@Test
	public void testParagraphWithoutOverlap() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("triethocmaclenin.docx");
		MockMultipartFile multipartFile = new MockMultipartFile("triethocmaclenin.docx", classPathResource.getInputStream());

		List<String> chunkList = chunkService.getChunkListFromMultipartFile(
				multipartFile,
				splitByParagraphWithoutOverlapStrategy
		);

		System.out.println("begin without overlap");
		chunkList.forEach(System.out::println);
		System.out.println("end without overlap");
	}

//	@Test
//	public void testLine() {
//		List<String> list = chunkService.getChunkListFromFilePath(
//				"src/main/resources/TTNT.docx",
//				splitByLineStrategy
//		);
//
//		System.out.println("line");
//		list.forEach(System.out::println);
//	}
//
//	@Test
//	public void testSentence() {
//		List<String> list = chunkService.getChunkListFromFilePath(
//				"src/main/resources/TTNT.docx",
//				splitBySentenceStrategy
//		);
//
//		System.out.println("sentence");
//		list.forEach(System.out::println);
//	}
//
//	@Test
//	public void testWord() {
//		List<String> list = chunkService.getChunkListFromFilePath(
//				"src/main/resources/TTNT.docx",
//				splitByWordStrategy
//		);
//
//		System.out.println("word");
//		list.forEach(System.out::println);
//	}

}