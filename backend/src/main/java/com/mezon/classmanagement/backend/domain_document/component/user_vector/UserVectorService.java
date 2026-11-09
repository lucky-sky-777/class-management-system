package com.mezon.classmanagement.backend.domain_document.component.user_vector;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.DocumentClusterMembershipService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.entity.DocumentBookmark;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.service.DocumentBookmarkService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.entity.DocumentDownload;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.service.DocumentDownloadService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.service.DocumentViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class UserVectorService {

	/**
	 * Service này dùng để tạo vector đại diện cho sở thích của User
	 */

	DocumentViewService documentViewService;
	DocumentDownloadService documentDownloadService;
	DocumentBookmarkService documentBookmarkService;

	DocumentClusterMembershipService documentClusterMembershipService;

	public void test(Long userId) {
		Instant now = Instant.now();

		List<ClusterInteraction> historyList =
				getClusterInteractionList2();

		double lambda = 0.1;

		Optional<List<ClusterPreference>> result =
				calculateClusterPreference(
						historyList,
						lambda,
						now
				);

		Consumer<List<ClusterPreference>> c = clusterPreferenceList -> {
			// Lấy Top-2 cụm được người dùng quan tâm nhất.
			List<ClusterPreference> top2Cluster = getTopClusterPreferenceList(
					clusterPreferenceList,
					2
			);

			double sum = 0.0d;
			for (ClusterPreference p : clusterPreferenceList) {
				sum += p.preference();
				System.out.println(p.clusterIndex() + ": " + p.preference());
			}
			System.out.println(sum);
		};

		result.ifPresent(c);
	}

	public List<ClusterPreference> getTopClusterPreferenceList(List<ClusterPreference> source, int limit) {
		List<ClusterPreference> sort = new ArrayList<>(source);
		sort.sort((o1, o2) -> Double.compare(
				o2.preference(),
				o1.preference()
		));

		int finalLimit = Math.min(limit, sort.size());

		List<ClusterPreference> top = new ArrayList<>(finalLimit);
		for (int i = 0; i < finalLimit; i++) {
			top.add(sort.get(i));
		}

		return top;
	}

	public List<ClusterInteraction> getClusterInteractionList2() {
		Instant now = Instant.now();

		return List.of(
				// 5 VIEW
				new ClusterInteraction(WeightConstant.VIEW, now.minus(Duration.ofDays(1)),
						List.of(new ClusterMembership(0, 0.8), new ClusterMembership(1, 0.1), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.VIEW, now.minus(Duration.ofDays(2)),
						List.of(new ClusterMembership(0, 0.7), new ClusterMembership(1, 0.2), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.VIEW, now.minus(Duration.ofDays(3)),
						List.of(new ClusterMembership(0, 0.1), new ClusterMembership(1, 0.8), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.VIEW, now.minus(Duration.ofDays(5)),
						List.of(new ClusterMembership(0, 0.2), new ClusterMembership(1, 0.2), new ClusterMembership(2, 0.6))),
				new ClusterInteraction(WeightConstant.VIEW, now.minus(Duration.ofDays(7)),
						List.of(new ClusterMembership(0, 0.5), new ClusterMembership(1, 0.3), new ClusterMembership(2, 0.2))),

				// 5 DOWNLOAD
				new ClusterInteraction(WeightConstant.DOWNLOAD, now.minus(Duration.ofDays(1)),
						List.of(new ClusterMembership(0, 0.9), new ClusterMembership(1, 0.05), new ClusterMembership(2, 0.05))),
				new ClusterInteraction(WeightConstant.DOWNLOAD, now.minus(Duration.ofDays(3)),
						List.of(new ClusterMembership(0, 0.1), new ClusterMembership(1, 0.8), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.DOWNLOAD, now.minus(Duration.ofDays(4)),
						List.of(new ClusterMembership(0, 0.2), new ClusterMembership(1, 0.7), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.DOWNLOAD, now.minus(Duration.ofDays(10)),
						List.of(new ClusterMembership(0, 0.1), new ClusterMembership(1, 0.1), new ClusterMembership(2, 0.8))),
				new ClusterInteraction(WeightConstant.DOWNLOAD, now.minus(Duration.ofDays(15)),
						List.of(new ClusterMembership(0, 0.6), new ClusterMembership(1, 0.2), new ClusterMembership(2, 0.2))),

				// 5 BOOKMARK
				new ClusterInteraction(WeightConstant.BOOKMARK, now.minus(Duration.ofDays(1)),
						List.of(new ClusterMembership(0, 0.7), new ClusterMembership(1, 0.2), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.BOOKMARK, now.minus(Duration.ofDays(2)),
						List.of(new ClusterMembership(0, 0.1), new ClusterMembership(1, 0.2), new ClusterMembership(2, 0.7))),
				new ClusterInteraction(WeightConstant.BOOKMARK, now.minus(Duration.ofDays(5)),
						List.of(new ClusterMembership(0, 0.2), new ClusterMembership(1, 0.6), new ClusterMembership(2, 0.2))),
				new ClusterInteraction(WeightConstant.BOOKMARK, now.minus(Duration.ofDays(8)),
						List.of(new ClusterMembership(0, 0.8), new ClusterMembership(1, 0.1), new ClusterMembership(2, 0.1))),
				new ClusterInteraction(WeightConstant.BOOKMARK, now.minus(Duration.ofDays(12)),
						List.of(new ClusterMembership(0, 0.1), new ClusterMembership(1, 0.7), new ClusterMembership(2, 0.2)))
		);
	}

	public List<ClusterInteraction> getClusterInteractionList(
			Long userId,
			Long modelId
	) {
		List<DocumentView> documentViewList = documentViewService
				.getRawListByUser(userId);
		List<DocumentDownload> documentDownloadList = documentDownloadService
				.getRawListByUser(userId);
		List<DocumentBookmark> documentBookmarkList = documentBookmarkService
				.getRawListByUser(userId);

		List<ClusterInteraction> historyList = new ArrayList<>();

		for (DocumentView documentView : documentViewList) {
			historyList.add(
					new ClusterInteraction(
							WeightConstant.VIEW,
							documentView.getCreatedAt(),
							documentClusterMembershipService
									.findAllByModelAndDocumentJoinFetchCluster(
											modelId,
											documentView.getDocument().getId()
									)
									.stream()
									.map(item ->
											new ClusterMembership(
													item.getCluster().getClusterIndex(),
													item.getMembership()
											)
									)
									.toList()
					)
			);
		}
		for (DocumentDownload documentDownload : documentDownloadList) {
			historyList.add(
					new ClusterInteraction(
							WeightConstant.DOWNLOAD,
							documentDownload.getCreatedAt(),
							documentClusterMembershipService
									.findAllByModelAndDocumentJoinFetchCluster(
											modelId,
											documentDownload.getDocument().getId()
									)
									.stream()
									.map(item ->
											new ClusterMembership(
													item.getCluster().getClusterIndex(),
													item.getMembership()
											)
									)
									.toList()
					)
			);
		}
		for (DocumentBookmark documentBookmark : documentBookmarkList) {
			historyList.add(
					new ClusterInteraction(
							WeightConstant.BOOKMARK,
							documentBookmark.getCreatedAt(),
							documentClusterMembershipService
									.findAllByModelAndDocumentJoinFetchCluster(
											modelId,
											documentBookmark.getDocument().getId()
									)
									.stream()
									.map(item ->
											new ClusterMembership(
													item.getCluster().getClusterIndex(),
													item.getMembership()
											)
									)
									.toList()
					)
			);
		}

		return historyList;
	}

	private static final double SECONDS_PER_DAY = 86_400.0;

	public record ClusterMembership(
			Integer clusterIndex,
			Double membership
	) {
	}

	public record ClusterInteraction(
			double behaviorWeight,
			Instant interactedAt,
			List<ClusterMembership> membership
	) {
	}

	public record ClusterPreference(
			Integer clusterIndex,
			double preference
	) {
	}

	/**
	 * Tính mức độ quan tâm của người dùng với mỗi cụm
	 *
	 * @param history Lịch sử tương tác
	 * @param lambda  Hệ số giảm ảnh hưởng theo ngày
	 * @param now     Thời điểm hiện tại
	 * @return Danh sách mức độ quan tâm mỗi cụm của người dùng
	 */
	public Optional<List<ClusterPreference>> calculateClusterPreference(
			List<ClusterInteraction> history,
			double lambda,
			Instant now
	) {
		if (!Double.isFinite(lambda) || lambda < 0) {
			throw new IllegalArgumentException("Lambda must be non-negative and finite");
		}

		if (history.isEmpty()) {
			return Optional.empty();
		}

		Set<Integer> expectedClusters = history.getFirst()
				.membership()
				.stream()
				.map(ClusterMembership::clusterIndex)
				.collect(Collectors.toSet());
		int clusterCount = history.getFirst().membership().size();

		if (clusterCount == 0) {
			throw new IllegalArgumentException("Membership must not be empty");
		}

		Map<Integer, Double> preferenceTree = new TreeMap<>();
		double totalWeight = 0.0;

		for (ClusterInteraction interaction : history) {
			Objects.requireNonNull(interaction, "Interaction must not be null");
			Objects.requireNonNull(interaction.interactedAt());
			Objects.requireNonNull(interaction.membership());

			if (interaction.membership().size() != clusterCount) {
				throw new IllegalArgumentException(
						"All membership vectors must have the same dimension"
				);
			}


//			Set<Integer> actualClusters = interaction.membership()
//					.stream()
//					.map(ClusterMembership::clusterIndex)
//					.collect(Collectors.toSet());
//
//			if (!actualClusters.equals(expectedClusters)) {
//				throw new IllegalArgumentException(
//						"All membership vectors must contain the same clusters"
//				);
//			}


			double behaviorWeight = interaction.behaviorWeight();

			if (!Double.isFinite(behaviorWeight) || behaviorWeight < 0) {
				throw new IllegalArgumentException(
						"Behavior weight must be non-negative and finite"
				);
			}

			// Khoảng thời gian từ lúc tương tác đến hiện tại (ngày).
			// Nếu timestamp nằm trong tương lai thì xem như tương tác tại hiện tại.
			double deltaTime = delta(interaction.interactedAt(), now);

			// q_d = w_d * e^(-lambda * delta_t)
			double q = behaviorWeight * Math.exp(-lambda * deltaTime);

			// Tổng hợp q_d * u_d cho từng cụm.

			for (ClusterMembership clusterMembership : interaction.membership()) {
				double value = clusterMembership.membership;

				if (!Double.isFinite(value) || value < 0) {
					throw new IllegalArgumentException(
							"Membership values must be non-negative and finite"
					);
				}

				preferenceTree.merge(
						clusterMembership.clusterIndex(),
						q * value,
						Double::sum
				);
			}

			totalWeight += q;
		}

		// Không thể tính vector nếu tổng trọng số bằng 0.
		if (totalWeight <= 0 || !Double.isFinite(totalWeight)) {
			return Optional.empty();
		}

		final double totalW = totalWeight;
		// Chia cho tổng trọng số.
		List<ClusterPreference> result = preferenceTree.entrySet()
				.stream()
				.map(entry -> new ClusterPreference(
						entry.getKey(),
						entry.getValue() / totalW
				))
				.toList();

		return Optional.of(result);
	}

	public record LongTermPreference(
			double[] preference
	) {
	}

	public record DocumentEmbedding(
			Long documentId,
			Vector1536 embedding
	) {
	}

	public record LongTermDocumentInteraction(
			double behaviorWeight,
			Instant interactedAt,
			DocumentEmbedding documentEmbedding
	) {
	}

	/**
	 * Tính vector mức độ quan tâm tổng thể của người dùng
	 *
	 * @param longTermDocumentInteractionList Lịch sử tương tác
	 * @param lambda  Hệ số giảm ảnh hưởng theo ngày
	 * @param now     Thời điểm hiện tại
	 * @return Vector đại diện cho mức độ quan tâm tổng thể của người dùng
	 */
	public Optional<LongTermPreference> calculateLongTermPreference(
			List<LongTermDocumentInteraction> longTermDocumentInteractionList,
			double lambda,
			Instant now
	) {
		if (!Double.isFinite(lambda) || lambda < 0) {
			throw new IllegalArgumentException("Lambda must be non-negative and finite");
		}

		if (longTermDocumentInteractionList.isEmpty()) {
			return Optional.empty();
		}

		int dimensionCount = longTermDocumentInteractionList.getFirst().documentEmbedding().embedding().getDimension();

		if (dimensionCount == 0) {
			throw new IllegalArgumentException("Membership must not be empty");
		}

		double[] totalVector = new double[dimensionCount];
		double totalWeight = 0.0;

		for (LongTermDocumentInteraction interaction : longTermDocumentInteractionList) {
			Objects.requireNonNull(interaction, "Interaction must not be null");
			Objects.requireNonNull(interaction.interactedAt());
			Objects.requireNonNull(interaction.documentEmbedding());

			if (interaction.documentEmbedding().embedding().getDimension() != dimensionCount) {
				throw new IllegalArgumentException(
						"All membership vectors must have the same dimension"
				);
			}

			double behaviorWeight = interaction.behaviorWeight();

			if (!Double.isFinite(behaviorWeight) || behaviorWeight < 0) {
				throw new IllegalArgumentException(
						"Behavior weight must be non-negative and finite"
				);
			}

			// Khoảng thời gian từ lúc tương tác đến hiện tại (ngày).
			// Nếu timestamp nằm trong tương lai thì xem như tương tác tại hiện tại.
			double deltaTime = delta(interaction.interactedAt(), now);

			// q_d = w_d * e^(-lambda * delta_t)
			double q = behaviorWeight * Math.exp(-lambda * deltaTime);

			// Tổng hợp q_d * e_d cho từng cụm.

			double[] multiplyVector = new double[dimensionCount];
			float[] e_d = interaction.documentEmbedding().embedding().getValues();
			multiplyVector = EmbeddingUtils.multiply(e_d, q);
			for (int i = 0; i < dimensionCount; i++) {
				totalVector[i] += multiplyVector[i];
			}

			totalWeight += q;
		}

		// Không thể tính vector nếu tổng trọng số bằng 0.
		if (totalWeight <= 0 || !Double.isFinite(totalWeight)) {
			return Optional.empty();
		}

		return Optional.of(
				new LongTermPreference(
						EmbeddingUtils.normalizeL2(
								EmbeddingUtils.divide(totalVector, totalWeight)
						)
				)
		);
	}

	public List<LongTermDocumentInteraction> getLongTermDocumentInteractionList(
			Long userId
	) {
		List<DocumentView> documentViewList = documentViewService
				.getRawListByUser(userId);
		List<DocumentDownload> documentDownloadList = documentDownloadService
				.getRawListByUser(userId);
		List<DocumentBookmark> documentBookmarkList = documentBookmarkService
				.getRawListByUser(userId);

		List<LongTermDocumentInteraction> historyList = new ArrayList<>();

		for (DocumentView documentView : documentViewList) {
			historyList.add(
					new LongTermDocumentInteraction(
							WeightConstant.VIEW,
							documentView.getCreatedAt(),
							new DocumentEmbedding(
									documentView.getDocument().getId(),
									documentView.getDocument().getEmbedding()
							)
					)
			);
		}
		for (DocumentDownload documentDownload : documentDownloadList) {
			historyList.add(
					new LongTermDocumentInteraction(
							WeightConstant.DOWNLOAD,
							documentDownload.getCreatedAt(),
							new DocumentEmbedding(
									documentDownload.getDocument().getId(),
									documentDownload.getDocument().getEmbedding()
							)
					)
			);
		}
		for (DocumentBookmark documentBookmark : documentBookmarkList) {
			historyList.add(
					new LongTermDocumentInteraction(
							WeightConstant.BOOKMARK,
							documentBookmark.getCreatedAt(),
							new DocumentEmbedding(
									documentBookmark.getDocument().getId(),
									documentBookmark.getDocument().getEmbedding()
							)
					)
			);
		}

		return historyList;
	}

	private static double delta(
			Instant past,
			Instant now
	) {
		return Math.max(
				0.0,
				Duration
						.between(past, now)
						.toMillis() / (SECONDS_PER_DAY * 1000.0)
		);
	}

}