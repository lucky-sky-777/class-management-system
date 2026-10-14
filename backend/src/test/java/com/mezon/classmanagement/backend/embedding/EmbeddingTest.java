package com.mezon.classmanagement.backend.embedding;

import com.mezon.classmanagement.backend.common.env.GeminiEnv;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.common.util.GeminiPromptBuilder;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.BaseVector;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class EmbeddingTest {

	EmbeddingService embeddingService;
	GeminiEnv geminiEnv;

	@Test
	public void embedSingleQueryTest() throws Exception {
		BaseVector vector = embeddingService.embedSingleQueryText("Spring Boot");

		System.out.println(vector.getDimension());
		EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
				.apiKey(geminiEnv.apiKey)
				.modelName("gemini-embedding-2")
				.outputDimensionality(1536)
				.build();

		Embedding embedding = embeddingModel
				.embed(GeminiPromptBuilder.buildQueryPrompt("Spring Boot")).content();

		float[] vector2 = embedding.vector();
		System.out.println(vector2.length);

		System.out.println(EmbeddingUtils.cosine(vector.getValues(), vector2));
	}

	@Test
	public void testDir() {
		System.out.println(System.getProperty("user.dir"));
	}

	@Test
	public void embedMediaTest() throws Exception {
		String fileName = "dog.png";
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		MockMultipartFile multipartFile = new MockMultipartFile(fileName, classPathResource.getInputStream());

		String query1 = "an image of a dog";
		String query2 = "java spring boot";

		BaseVector query1Vector = embeddingService.embedSingleQueryText(query1);
		BaseVector query2Vector = embeddingService.embedSingleQueryText(query2);
		BaseVector resultVector = embeddingService.embedSingleMedia(multipartFile);

		float cosine1 = EmbeddingUtils.cosine(query1Vector.getValues(), resultVector.getValues());
		float cosine2 = EmbeddingUtils.cosine(query2Vector.getValues(), resultVector.getValues());

		System.out.println(cosine1 + " - " + cosine2);
	}

//	@Test
//	public void embedMultipleQueryTest() throws Exception {
//		String question = "AI giúp ích thế nào cho các lập trình viên?";
//
//		String text1 = "AI giúp các lập trình viên làm việc nhanh hơn.";
//		String text2 = "Trí tuệ nhân tạo giúp các nhà phát triển thực hiện công việc ít thời gian hơn.";
//		String text3 = "Các nhà phát triển thực hiện công việc ít thời gian hơn nhờ trí tuệ nhân tạo.";
//
//		BaseVector questionQueryVector = embeddingService.embedSingleQueryText(question);
//		BaseVector questionDocumentVector = embeddingService.embedSingleDocumentText("null", question);
//
//		List<BaseVector> vectorList1 = embeddingService.embedMultipleQueryText(
//				List.of(text1, text2, text3)
//		);
//		List<BaseVector> vectorList2 = embeddingService.embedMultipleDocumentText(
//				List.of(
//						Pair.of("null", text1),
//						Pair.of("null", text2),
//						Pair.of("null", text3)
//				)
//		);
//
//		BaseVector vector11 = vectorList1.get(0);
//		BaseVector vector12 = vectorList1.get(1);
//		BaseVector vector13 = vectorList1.get(2);
//
//		BaseVector vector21 = vectorList2.get(0);
//		BaseVector vector22 = vectorList2.get(1);
//		BaseVector vector23 = vectorList2.get(2);
//
//		float f1 = EmbeddingUtils.cosine(
//				questionQueryVector.getValues(),
//				vector11.getValues()
//		);
//		float f2 = EmbeddingUtils.cosine(
//				questionDocumentVector.getValues(),
//				vector11.getValues()
//		);
//		float f3 = EmbeddingUtils.cosine(
//				questionQueryVector.getValues(),
//				vector21.getValues()
//		);
//		float f4 = EmbeddingUtils.cosine(
//				questionDocumentVector.getValues(),
//				vector21.getValues()
//		);
//		System.out.println(f1);
//		System.out.println(f2);
//		System.out.println(f3);
//		System.out.println(f4);
//
//		//assert f1 > 0.9 : "required > 0.9, got: " + f1;
//		//assert f2 < 0.6 : "required < 0.6, got: " + f2;
//
////		System.out.println(f1);
////		assert f1 > 0.9 : "required > 0.9, got: " + f1;
////		System.out.println(f2);
////		assert f2 < 0.7 : "required < 0.7, got: " + f2;
////
////		Assertions.assertTrue(f1 > 0.9, "must be greater than 0.9");
////		Assertions.assertTrue(f2 < 0.7, "must be less than 0.7");
////		Assertions.assertTrue(f2 < 0.6, "must be less than 0.6");
//	}

}