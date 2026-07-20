package com.mezon.classmanagement.backend_document;

import com.mezon.classmanagement.backend_document.common.util.FileUtils;
import com.mezon.classmanagement.backend_document.domain.component.tag.service.TagService;
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

	@Test
	public void wsegTest() throws Exception {
		//List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.readResourceFile("input.txt"), 3);
		List<String> tagList = tagService.extractTagsWithVnCoreNLP(FileUtils.getContent("Ho_Chi_Minh.pdf"), 10);

		System.out.println("==========\n");
		tagList.forEach(System.out::println);
		System.out.println("\n==========");
	}

}