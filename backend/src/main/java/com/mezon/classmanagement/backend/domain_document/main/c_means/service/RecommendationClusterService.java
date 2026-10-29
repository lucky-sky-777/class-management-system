package com.mezon.classmanagement.backend.domain_document.main.c_means.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.repository.RecommendationClusterRepository;
import com.mezon.classmanagement.backend.domain_document.main.c_means.util.CMeansUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RecommendationClusterService {

	RecommendationClusterRepository repository;

	@Transactional
	public List<RecommendationCluster> createList(
			int clusterCount,
			List<double[]> centerList,
			RecommendationModel model
	) {
		List<RecommendationCluster> clusterList =
				new ArrayList<>(clusterCount);

		for (int clusterIndex = 0; clusterIndex < clusterCount; clusterIndex++) {

			double[] center = centerList.get(clusterIndex);

			CMeansUtils.validateCenter(center, clusterIndex);

			RecommendationCluster cluster =
					RecommendationCluster.builder()
							.model(model)
							.clusterIndex(clusterIndex)
							.centerEmbedding(
									new Vector1536(EmbeddingUtils.toFloatArray(center))
							)
							.build();

			clusterList.add(cluster);
		}

		List<RecommendationCluster> savedClusters =
				repository.saveAllAndFlush(clusterList);

		return savedClusters;
	}

	@Transactional(readOnly = true)
	public List<RecommendationCluster> getList() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public List<RecommendationCluster> findByRecommendationModelIdOrThrow(
			Long recommendationModelId
	) {
		return repository
				.findAllByModel_IdOrderByClusterIndexAsc(recommendationModelId);
	}

	@Transactional(readOnly = true)
	public RecommendationCluster findByRecommendationModelIdAndClusterIndexOrThrow(
			Long recommendationModelId,
			Integer clusterIndex
	) {
		return repository
				.findByModel_IdAndClusterIndexOrderByClusterIndexAsc(
						recommendationModelId,
						clusterIndex
				)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_EXISTS)
				);
	}

}