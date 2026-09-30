package com.mezon.classmanagement.backend.embedding;

import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
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
public class EmbeddingTest {

	EmbeddingService embeddingService;

	//@Test
	public void embedSingleQueryTest() throws Exception {
		Vector1536 vector1536 = embeddingService.embedSingleQueryText("Spring Boot");
		System.out.println(embeddingService.toVectorString(vector1536));
	}

	@Test
	public void embedMultipleQueryTest() throws Exception {
		List<Vector1536> vector1536List = embeddingService.embedMultipleQueryText(
				List.of(
						"Spring Boot",
						"Jpa",
						"Hibernate"
				)
		);

		vector1536List.forEach(item -> System.out.println(embeddingService.toVectorString(item)));
	}

}