package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.impl.paragraph.SplitByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.tag.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class VnCoreNLPTest {

	TagService tagService;
	ChunkService chunkService;

	SplitByParagraphStrategy splitByParagraphStrategy;
	SplitByParagraphWithoutOverlapStrategy splitByParagraphWithoutOverlapStrategy;

	@Test
	public void wsegTest() throws Exception {
		//List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.readResourceFile("input.txt"), 3);
		List<String> chunkList = chunkService.getChunkListFromFilePath(
				"src/main/resources/Ho_Chi_Minh.docx",
				splitByParagraphWithoutOverlapStrategy
		);
		List<String> tagList = tagService.extractTagsWithVnCoreNLP(chunkList, 10);

		System.out.println("==========\n");
		tagList.forEach(System.out::println);
		System.out.println("\n==========");
	}

	@Test
	public void wsegTest2() throws Exception {
		//List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.readResourceFile("input.txt"), 3);

		List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.getContent("Ho_Chi_Minh.docx"), 10);

		System.out.println("==========\n");
		tagList.forEach(System.out::println);
		System.out.println("\n==========");
	}

}