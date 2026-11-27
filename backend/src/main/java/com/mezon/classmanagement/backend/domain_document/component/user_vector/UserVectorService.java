package com.mezon.classmanagement.backend.domain_document.component.user_vector;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.DocumentClusterMembershipService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.entity.DocumentBookmark;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_bookmark.service.DocumentBookmarkService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.entity.DocumentDownload;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.service.DocumentDownloadService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.service.DocumentViewService;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.PreferenceCalculator;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterInteraction;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterMembership;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterPreference;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
		preferenceCalculator.calculateUserPreferenceWithEachCluster(
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









	PreferenceCalculator preferenceCalculator;

	/**
	 * Lấy danh sách mức độ quan tâm của người dùng với mỗi cụm
	 * @param userId ID người dùng cần lấy danh sách
	 * @param latestModelId ID mô hình phân cụm mới nhất
	 * @return Danh sách mức độ quan tâm của người dùng với mỗi cụm
	 */
	public Optional<List<ClusterPreference>> getPreferencePerCluster(
			Long userId,
			Long latestModelId
	) {
		return preferenceCalculator.calculateUserPreferenceWithEachCluster(
				getClusterInteractionList(
						userId,
						latestModelId
				),
				0.1,
				Instant.now()
		);
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

}