package com.mezon.classmanagement.backend.domain_document.main.recommendation.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.DocumentClusterMembership;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationCluster;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.DocumentClusterMembershipService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationClusterService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationModelService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RecommendationService {

	UserService userService;
	DocumentService documentService;
	RecommendationClusterService recommendationClusterService;
	RecommendationModelService recommendationModelService;
	DocumentClusterMembershipService documentClusterMembershipService;

	public List<DocumentResponseDto> getDocumentRecommendationList(Long userId) {
		User user = userService.findByIdOrThrow(userId);

		Vector1536 x_u = user.getEmbedding();

		RecommendationModel latestModel = recommendationModelService
				.findLatestReadyModelOrThrow();

		/*
		 * Tất cả tâm cụm
		 */
		List<Vector1536> cList = getCList(latestModel.getId());

		/*
		 * Khoảng cách từ User đến từng tâm cụm
		 */
		List<Float> d_uList = getD_uList(x_u, cList);

		/*
		 * Mức độ thuộc cụm của User với mỗi tâm cụm
		 */
		List<Float> micro_ukList = getMicro_ukList(d_uList, latestModel.getFuzziness());

		/*
		 * Cluster liên quan nhất với User
		 */
		RecommendationCluster bestCluster = recommendationClusterService
				.findByRecommendationModelIdAndClusterIndexOrThrow(
						latestModel.getId(),
						getBestClusterIndex(micro_ukList)
				);

		return documentService
				.findTopPublicDocumentInCluster(bestCluster.getId(), 5);
	}

	private int getBestClusterIndex(List<Float> membershipList) {
		int maxIndex = 0;
		Float maxValue = membershipList.getFirst();
		for (int i = 1; i < membershipList.size(); i++) {
			if (membershipList.get(i) > maxValue) {
				maxIndex = i;
				maxValue = membershipList.get(i);
			}
		}
		return maxIndex;
	}

	/*
	 * Bước 1: Tính mức độ thuộc cụm của User
	 */

	/**
	 * Lấy danh sách tâm cụm
	 * <p>
	 * [c1, c2, c3, ..., cC]
	 *
	 * @return {@code List<Vector1536>} Danh sách tâm cụm
	 */
	private List<Vector1536> getCList(Long recommendationModelId) {
		return recommendationClusterService
				.findByRecommendationModelIdOrThrow(recommendationModelId)
				.stream()
				.map(RecommendationCluster::getCenterEmbedding)
				.toList();
	}

	/**
	 * Lấy danh sách khoảng cách từ embedding {@code x_u} của User
	 * <p>
	 * đến từng tâm cụm trong {@code cList}.
	 * <p>
	 * Công thức cho mỗi phần tử:
	 * <p>
	 * d_uk = ||x_u - c_k||
	 * <p>
	 *
	 * @param x_u   embedding của user
	 * @param cList danh sách embedding của các tâm cụm
	 * @return {@code List<Float>} Danh sách khoảng cách từ embedding của user {@code x_u}
	 * <p>
	 * đến mỗi embedding của các tâm cụm {@code cList}
	 */
	private List<Float> getD_uList(
			Vector1536 x_u,
			List<Vector1536> cList
	) {
		int cListSize = cList.size();
		List<Float> d_uList = new ArrayList<>(cListSize);

		for (Vector1536 vector1536 : cList) {
			d_uList.add(EmbeddingUtils.euclideanDistance(
					x_u.getValues(),
					vector1536.getValues()
			));
		}

		return d_uList;
	}

	/**
	 * Lấy danh sách mức độ thuộc cụm từ {@code d_uList}
	 * <p>
	 * μ_uk = 1 / (Σ(j = 1, c))
	 *
	 * @param d_uList Danh sách khoảng cách từ embedding của User
	 *                <p>đến từng tâm cụm
	 * @param m fuzziness
	 * @return
	 */
	private List<Float> getMicro_ukList(
			List<Float> d_uList,
			double m
	) {
		int d_uListSize = d_uList.size();
		List<Float> micro_ukList = new ArrayList<>(d_uListSize);

		for (int k = 0; k < d_uListSize; k++) {
			micro_ukList.add(
					EmbeddingUtils.membership(d_uList, k, m)
			);
		}

		return micro_ukList;
	}

}