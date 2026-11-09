package com.mezon.classmanagement.backend.c_means;

import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.user_vector.UserVectorService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.DocumentEmbeddingList;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.FcmTrainingResult;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.RecommendationModelRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.DocumentClusterMembershipRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationClusterRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationModelRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.DocumentClusterMembershipService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.FuzzyCMeansService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationClusterService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationModelService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.util.CMeansUtils;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.service.RecommendationService;
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
public class CMeansTest {

	EmbeddingService embeddingService;
	FuzzyCMeansService fuzzyCMeansService;
	RecommendationClusterService recommendationClusterService;
	RecommendationModelService recommendationModelService;
	DocumentClusterMembershipService documentClusterMembershipService;
	DocumentService documentService;
	RecommendationModelRepository recommendationModelRepository;

	RecommendationClusterRepository recommendationClusterRepository;
	DocumentClusterMembershipRepository documentClusterMembershipRepository;
	RecommendationService recommendationService;

	UserVectorService userVectorService;

	@Test
	public void testUserVector() {
		userVectorService.test(5L);
	}

	private static final int SAVE_BATCH_SIZE = 1000;

	@Test
	public void testApiKey() {
		embeddingService.ping();
	}

	@Test
	public void tesRCM() throws Exception {
		String text1 = "lập trình java";
		String text2 = "cá chìa vôi mắt đơn";

		Vector1536 x_u = new Vector1536(
				embeddingService
				.embedSingleQueryText(text2).getValues()
		);

		List<DocumentResponseDto> r = recommendationService.get2(x_u);

		r.forEach(item -> {
			System.out.println(item.getFileName());
		});
	}

	@Test
	public void testTrain() {
		DocumentEmbeddingList documentEmbeddingList = documentService.getEmbeddingList();

		// 1. Huấn luyện Fuzzy C-Means
		FcmTrainingResult result = fuzzyCMeansService
				.train(
						EmbeddingUtils.toDoubleList(
								documentEmbeddingList.embeddingList()
						),
						2
				);

		double[][] memberships = result.membershipMatrix();
		List<double[]> centerList = result.centerList();
		int[] dominantCluster = result.dominantCluster();

		int documentCount = documentEmbeddingList.idList().size();
		int clusterCount = centerList.size();

		CMeansUtils.validateTrainingResult(result, documentCount);
		CMeansUtils.validateMembership(memberships, clusterCount);

		RecommendationModel model = recommendationModelService.create(
				new RecommendationModelRequestDto(clusterCount, RecommendationModel.Status.TRAINING)
		);

		// 3. Lưu các tâm cụm để lấy cluster.id

		List<RecommendationCluster> savedClusters = recommendationClusterService
				.createList(clusterCount, centerList, model);

		// 4. Lưu mức độ thuộc cụm của từng tài liệu theo từng cluster.
		// Mỗi tài liệu có clusterCount bản ghi membership.

		documentClusterMembershipService
				.create(
						savedClusters,
						clusterCount,
						documentCount,
						documentEmbeddingList,
						memberships,
						dominantCluster
				);

		// 5. Chỉ đánh dấu READY sau khi lưu xong kết quả

		recommendationModelService.update(model.getId(), RecommendationModel.Status.READY);

		System.out.println("Huấn luyện Fuzzy C-Means hoàn tất.");
		System.out.println("Model ID: " + model.getId());
		System.out.println("Số tài liệu: " + documentCount);
		System.out.println("Số cụm: " + clusterCount);
		System.out.println(
				"Số bản ghi membership: "
						+ ((long) documentCount * clusterCount)
		);
	}

}