package com.mezon.classmanagement.backend.c_means;

import com.mezon.classmanagement.backend.config.EmbeddingConfig;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.DocumentClusterMembershipRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationClusterRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationModelRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.FuzzyCMeansService;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class CMeansTest {

	FuzzyCMeansService fuzzyCMeansService;
	DocumentService documentService;

	public float[] toFloatArray(double[] vector) {
		float[] result = new float[vector.length];

		for (int i = 0; i < vector.length; i++) {
			result[i] = (float) vector[i];
		}

		return result;
	}

	public double[] toDoubleArray(float[] vector) {
		double[] result = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			result[i] = vector[i];
		}

		return result;
	}

	public List<double[]> toDoubleList(
			List<float[]> embeddings) {

		List<double[]> result = new ArrayList<>(embeddings.size());

		for (float[] embedding : embeddings) {
			result.add(toDoubleArray(embedding));
		}

		return result;
	}

	RecommendationModelRepository recommendationModelRepository;

	RecommendationClusterRepository recommendationClusterRepository;
	DocumentClusterMembershipRepository documentClusterMembershipRepository;

	private static final int SAVE_BATCH_SIZE = 1000;

	@Test
	public void test() {
		List<Document> documentList = documentService.getAll();

		if (documentList.isEmpty()) {
			throw new IllegalStateException("Không có tài liệu để huấn luyện");
		}

		List<Long> documentIdList = new ArrayList<>();
		List<float[]> embeddingList = new ArrayList<>();

		for (Document document : documentList) {
			if (document.getEmbedding() == null || document.getEmbedding().getValues() == null) {
				throw new IllegalStateException(
						"Tài liệu chưa có embedding: " + document.getId()
				);
			}

			float[] embedding = document.getEmbedding().getValues();

			if (embedding.length != EmbeddingConfig.EMBEDDING_DIMENSION) {
				throw new IllegalStateException(
						"Embedding không đúng 1536 chiều, documentId: "
								+ document.getId()
				);
			}

			documentIdList.add(document.getId());
			embeddingList.add(embedding);
		}

		// 1. Huấn luyện Fuzzy C-Means
		FuzzyCMeansService.FcmTrainingResult result =
				fuzzyCMeansService.train(toDoubleList(embeddingList));

		double[][] memberships = result.membershipMatrix();
		List<double[]> centers = result.centerList();
		int[] dominantCluster = result.dominantCluster();

		int documentCount = documentIdList.size();
		int clusterCount = centers.size();

		if (memberships.length != documentCount || dominantCluster.length != documentCount || clusterCount == 0) {
			throw new IllegalStateException(
					"Kết quả huấn luyện không khớp với dữ liệu đầu vào"
			);
		}

		for (double[] membership : memberships) {
			if (membership == null || membership.length != clusterCount) {
				throw new IllegalStateException(
						"Ma trận membership không hợp lệ"
				);
			}
		}

		// 2. Lưu thông tin mô hình trước để lấy model.id
		RecommendationModel model = RecommendationModel.builder()
				.embeddingModel(EmbeddingConfig.EMBEDDING_MODEL)
				.embeddingDimension(EmbeddingConfig.EMBEDDING_DIMENSION)
				.clusterCount(clusterCount)
				.fuzziness(2.0)
				.minIterations(5)
				.maxIterations(100)
				.convergenceDelta(1e-6)
				.status(RecommendationModel.Status.TRAINING)
				.build();

		model = recommendationModelRepository.saveAndFlush(model);

		// 3. Lưu các tâm cụm để lấy cluster.id
		List<RecommendationCluster> clusterList =
				new ArrayList<>(clusterCount);

		for (int clusterIndex = 0;
		     clusterIndex < clusterCount;
		     clusterIndex++) {

			double[] center = centers.get(clusterIndex);

			if (center.length != EmbeddingConfig.EMBEDDING_DIMENSION) {
				throw new IllegalStateException(
						"Tâm cụm không đúng 1536 chiều: " + clusterIndex
				);
			}

			RecommendationCluster cluster =
					RecommendationCluster.builder()
							.model(model)
							.clusterIndex(clusterIndex)
							.centerEmbedding(
									new Vector1536(toFloatArray(center))
							)
							.build();

			clusterList.add(cluster);
		}

		List<RecommendationCluster> savedClusters =
				recommendationClusterRepository.saveAllAndFlush(clusterList);

		// 4. Lưu mức độ thuộc cụm của từng tài liệu theo từng cluster.
		// Mỗi tài liệu có clusterCount bản ghi membership.
		List<DocumentClusterMembership> membershipBatch =
				new ArrayList<>(SAVE_BATCH_SIZE);

		for (int i = 0; i < documentCount; i++) {
			Long documentId = documentIdList.get(i);

			double[] membershipOfDocument = memberships[i];
			int dominant = dominantCluster[i];

			if (dominant < 0 || dominant >= clusterCount) {
				throw new IllegalStateException(
						"Dominant cluster không hợp lệ cho documentId: "
								+ documentId
				);
			}

			for (int clusterIndex = 0;
			     clusterIndex < clusterCount;
			     clusterIndex++) {

				double membership = membershipOfDocument[clusterIndex];

				if (!Double.isFinite(membership)
						|| membership < 0.0
						|| membership > 1.0) {
					throw new IllegalStateException(
							"Membership không hợp lệ, documentId: "
									+ documentId
					);
				}

				DocumentClusterMembership item =
						DocumentClusterMembership.builder()
								.documentId(Document.builder().id(documentId).build())
								.clusterId(
										RecommendationCluster.builder().id(savedClusters.get(clusterIndex).getId()).build()
								)
								.membership(membership)
								.build();

				membershipBatch.add(item);

				// Lưu theo từng lô để hạn chế bộ nhớ sử dụng
				if (membershipBatch.size() >= SAVE_BATCH_SIZE) {
					documentClusterMembershipRepository
							.saveAll(membershipBatch);

					membershipBatch.clear();
				}
			}
		}

		// Lưu các bản ghi còn lại
		if (!membershipBatch.isEmpty()) {
			documentClusterMembershipRepository.saveAll(membershipBatch);
		}

		// 5. Chỉ đánh dấu READY sau khi lưu xong kết quả
		model.setStatus(RecommendationModel.Status.READY);
		model.setTrainedAt(Instant.now());

		recommendationModelRepository.save(model);

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