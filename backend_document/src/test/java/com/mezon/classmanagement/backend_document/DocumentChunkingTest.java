package com.mezon.classmanagement.backend_document;

import com.mezon.classmanagement.backend_document.common.constant.FileConstant;
import com.mezon.classmanagement.backend_document.domain.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByAllStrategy;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByLineStrategy;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitBySentenceStrategy;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.impl.SplitByWordStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class DocumentChunkingTest {

	ChunkService chunkService;

	SplitByAllStrategy splitByAllStrategy;
	SplitByParagraphStrategy splitByParagraphStrategy;
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

	/**
	 * Nên dùng cái này
	 */
	@Test
	public void testParagraph() {
		List<String> list = chunkService.getChunkListFromFilePath(
				"src/main/resources/The-gioi-5000-nam-nhung-dieu-bi-an.pdf",
				splitByParagraphStrategy
		);

		System.out.println(FileConstant.AllowedMimeType.TXT);
		System.out.println("paragraph");
		list.forEach(System.out::println);
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