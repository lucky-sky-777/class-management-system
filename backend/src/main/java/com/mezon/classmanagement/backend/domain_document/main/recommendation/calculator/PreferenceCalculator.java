package com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.component.user_vector.UserVectorService;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterInteraction;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterMembership;
import com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto.ClusterPreference;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PreferenceCalculator {

	/**
	 * Tính mức độ quan tâm của người dùng với mỗi cụm
	 *
	 * @param history Lịch sử tương tác
	 * @param lambda  Hệ số giảm ảnh hưởng theo ngày
	 * @param now     Thời điểm hiện tại
	 * @return Danh sách mức độ quan tâm mỗi cụm của người dùng
	 */
	public Optional<List<ClusterPreference>> calculateUserPreferenceWithEachCluster(
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
				double value = clusterMembership.membership();

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

	/**
	 * Tính vector mức độ quan tâm tổng thể của người dùng
	 *
	 * @param longTermDocumentInteractionList Lịch sử tương tác
	 * @param lambda  Hệ số giảm ảnh hưởng theo ngày
	 * @param now     Thời điểm hiện tại
	 * @return Vector đại diện cho mức độ quan tâm tổng thể của người dùng
	 */
	public Optional<UserVectorService.LongTermPreference> calculateUserPreferenceWithOverall(
			List<UserVectorService.LongTermDocumentInteraction> longTermDocumentInteractionList,
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

		for (UserVectorService.LongTermDocumentInteraction interaction : longTermDocumentInteractionList) {
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
				new UserVectorService.LongTermPreference(
						EmbeddingUtils.normalizeL2(
								EmbeddingUtils.divide(totalVector, totalWeight)
						)
				)
		);
	}

	private static final double SECONDS_PER_DAY = 86_400.0;

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
