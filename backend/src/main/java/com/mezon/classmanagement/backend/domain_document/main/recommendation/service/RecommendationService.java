package com.mezon.classmanagement.backend.domain_document.main.recommendation.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.user_vector.UserVectorService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.entity.RecommendationModel;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.RecommendationModelService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.RecommendationProjection;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.PreferenceCalculator;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterPreference;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.repository.RecommendationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RecommendationService {

	DocumentService documentService;
	RecommendationModelService recommendationModelService;
	UserVectorService userVectorService;
	RecommendationRepository recommendationRepository;

	PreferenceCalculator preferenceCalculator;

	/**
	 * Lấy danh sách tài liệu ứng viên
	 *
	 * @param userId ID người dùng cần lấy danh sách
	 * @return Danh sách các tài liệu tìm được
	 */
	public List<RecommendationProjection> getCandidate(Long userId) {

		List<RecommendationProjection> candidateSource1 = getCandidateSource1(userId);
		List<RecommendationProjection> candidateSource2 = getCandidateSource2(userId);

		// Hợp nhất các danh sách, loại bỏ các tài liệu trùng
		return merge(candidateSource1, candidateSource2);
	}

	@SafeVarargs
	public final List<RecommendationProjection> merge(List<RecommendationProjection>... src) {
		return new ArrayList<>(
				Stream.of(src)
						.flatMap(List::stream)
						.collect(
								Collectors.toMap(
										RecommendationProjection::getId,
										Function.identity(),
										(a, b) -> a,
										LinkedHashMap::new
								))
						.values()
		);
	}

	/**
	 * Lấy danh sách ứng viên dựa vào cụm
	 */
	public List<RecommendationProjection> getCandidateSource1(
			Long userId
	) {
		RecommendationModel latestModel = recommendationModelService
				.findLatestReadyModelOrThrow();
		System.out.println("model id: " + latestModel.getId());

		List<RecommendationProjection> result = new ArrayList<>();

		// 1. Lấy danh sách mức độ quan tâm của người dùng với mỗi cụm
		Optional<List<ClusterPreference>> optionalClusterPreferenceList =
				userVectorService.getPreferencePerCluster(userId, latestModel.getId());

		Consumer<List<ClusterPreference>> c = clusterPreferenceList -> {
			// 2. Lấy Top-2 cụm được người dùng quan tâm nhất.
			List<ClusterPreference> top2Cluster = userVectorService.getTopClusterPreferenceList(
					clusterPreferenceList,
					2
			);

			// 3. Với mỗi cụm, lấy Top-2 tài liệu có membership cao nhất.
			for (ClusterPreference cluster : top2Cluster) {
				result.addAll(
						documentService
								.findTopPublicDocumentInClusterIndexByModelId(
										cluster.clusterIndex(),
										latestModel.getId(),
										2
								)
				);
			}
		};

		optionalClusterPreferenceList.ifPresent(c);

		return result;
	}

	/**
	 * Lấy danh sách ứng viên dựa vào lịch sử tương tác
	 */
	public List<RecommendationProjection> getCandidateSource2(Long userId) {
		List<RecommendationProjection> result = new ArrayList<>();

		List<UserVectorService.LongTermDocumentInteraction> longTermDocumentInteractionList =
				userVectorService.getLongTermDocumentInteractionList(
						userId
				);

		Optional<UserVectorService.LongTermPreference> longTermPreference =
				preferenceCalculator.calculateUserPreferenceWithOverall(
						longTermDocumentInteractionList,
						0.1,
						Instant.now()
				);

		Consumer<UserVectorService.LongTermPreference> longTermPreferenceConsumer = longTermPreference1 -> result.addAll(
				documentService.findAllByEmbeddingCompare(
						userId,
						EmbeddingUtils.toFloatArray(longTermPreference1.preference()),
						20
				)
		);

		longTermPreference.ifPresent(longTermPreferenceConsumer);

		return result;
	}

//	public List<DocumentResponseDto> get2(Vector1536 x_u) {
//		RecommendationModel latestModel = recommendationModelService
//				.findLatestReadyModelOrThrow();
//
//		/*
//		 * Tất cả tâm cụm
//		 */
//		List<RecommendationCluster> cList = getCList(latestModel.getId());
//		System.out.println("danh sach tam cum");
//		cList.forEach(item -> System.out.println(item.getCenterEmbedding().getValues().length));
//
//		/*
//		 * Khoảng cách từ User đến từng tâm cụm
//		 */
//		List<DistanceFromUserToClusterCenter> d_uList = getD_uList(x_u, cList);
//		System.out.println("khoang cach user den tung tam cum");
//		d_uList.sort((o1, o2) -> o2.distance().compareTo(o1.distance()));
//		d_uList.forEach(item -> System.out.println(item.distance()));
//
//		/*
//		 * Mức độ thuộc cụm của User với mỗi cụm
//		 */
//		List<MembershipOfUserWithCluster> micro_ukList = getMicro_ukList(d_uList, latestModel.getFuzziness());
//		System.out.println("muc do thuoc cum cua user voi moi cum");
//		micro_ukList.sort((o1, o2) -> o2.membership().compareTo(o1.membership()));
//		micro_ukList.forEach(item -> System.out.println(item.membership()));
//
//		List<DocumentResponseDto> topDocumentList1 = new ArrayList<>(
//				documentService
//						.findTopPublicDocumentInClusterIndexByModelId(
//								micro_ukList.get(0).clusterIndex(),
//								latestModel.getId(),
//								2
//						)
//		);
//
//		List<DocumentResponseDto> topDocumentList2 = new ArrayList<>(
//				documentService
//						.findTopPublicDocumentInClusterIndexByModelId(
//								micro_ukList.get(1).clusterIndex(),
//								latestModel.getId(),
//								2
//						)
//		);
//
//		topDocumentList1.addAll(topDocumentList2);
//
//		return topDocumentList1;
//	}
//
//	public List<DocumentResponseDto> getDocumentRecommendationList(Long userId) {
//		User user = userService.findByIdOrThrow(userId);
//
//		Vector1536 x_u = user.getEmbedding();
//
//		RecommendationModel latestModel = recommendationModelService
//				.findLatestReadyModelOrThrow();
//
//		/*
//		 * Tất cả tâm cụm
//		 */
//		List<Vector1536> cList = getCList(latestModel.getId());
//
//		/*
//		 * Khoảng cách từ User đến từng tâm cụm
//		 */
//		List<Float> d_uList = getD_uList(x_u, cList);
//
//		/*
//		 * Mức độ thuộc cụm của User với mỗi tâm cụm
//		 */
//		List<Float> micro_ukList = getMicro_ukList(d_uList, latestModel.getFuzziness());
//
//		/*
//		 * Cluster liên quan nhất với User
//		 */
//		RecommendationCluster bestCluster = recommendationClusterService
//				.findByRecommendationModelIdAndClusterIndexOrThrow(
//						latestModel.getId(),
//						getBestClusterIndex(micro_ukList)
//				);
//
//		return documentService
//				.findTopPublicDocumentInCluster(bestCluster.getId(), 5);
//	}
//
//	/*
//	 * Bước 1: Tính mức độ thuộc cụm của User
//	 */
//
//	/**
//	 * Lấy danh sách tâm cụm
//	 * <p>
//	 * [c1, c2, c3, ..., cC]
//	 *
//	 * @return {@code List<Vector1536>} Danh sách tâm cụm
//	 */
//	private List<RecommendationCluster> getCList(Long recommendationModelId) {
//		return recommendationClusterService
//				.findAllByRecommendationModelId(recommendationModelId);
//	}
//
//	/**
//	 * Lấy danh sách khoảng cách từ embedding {@code x_u} của User
//	 * <p>
//	 * đến từng tâm cụm trong {@code cList}.
//	 * <p>
//	 * Công thức cho mỗi phần tử:
//	 * <p>
//	 * d_uk = ||x_u - c_k||
//	 * <p>
//	 *
//	 * @param x_u   embedding của user
//	 * @param cList danh sách các tâm cụm
//	 * @return {@code List<Float>} Danh sách khoảng cách từ embedding của user {@code x_u}
//	 * <p>
//	 * đến mỗi embedding của các tâm cụm {@code cList}
//	 */
//	private List<DistanceFromUserToClusterCenter> getD_uList(
//			Vector1536 x_u,
//			List<RecommendationCluster> cList
//	) {
//		int cListSize = cList.size();
//		List<DistanceFromUserToClusterCenter> d_uList = new ArrayList<>(cListSize);
//
//		for (RecommendationCluster c : cList) {
//			d_uList.add(
//					new DistanceFromUserToClusterCenter(
//							x_u,
//							c.getClusterIndex(),
//							EmbeddingUtils.euclideanDistance(
//									x_u.getValues(),
//									c.getCenterEmbedding().getValues()
//							)
//					)
//			);
//		}
//
//		return d_uList;
//	}
//
//	/**
//	 * Lấy danh sách mức độ thuộc cụm từ {@code d_uList}
//	 * <p>
//	 * μ_uk = 1 / (Σ(j = 1, c))
//	 *
//	 * @param d_uList Danh sách khoảng cách từ embedding của User
//	 *                <p>đến từng tâm cụm
//	 * @param m       fuzziness
//	 * @return
//	 */
//	private List<MembershipOfUserWithCluster> getMicro_ukList(
//			List<DistanceFromUserToClusterCenter> d_uList,
//			double m
//	) {
//		int d_uListSize = d_uList.size();
//		List<MembershipOfUserWithCluster> micro_ukList = new ArrayList<>(d_uListSize);
//
//		for (DistanceFromUserToClusterCenter d_u : d_uList) {
//			micro_ukList.add(
//					new MembershipOfUserWithCluster(
//							d_u.userEmbedding(),
//							d_u.clusterIndex(),
//							EmbeddingUtils.membership(
//									d_uList,
//									d_u.clusterIndex(),
//									m
//							)
//					)
//			);
//		}
//
//		return micro_ukList;
//	}

}