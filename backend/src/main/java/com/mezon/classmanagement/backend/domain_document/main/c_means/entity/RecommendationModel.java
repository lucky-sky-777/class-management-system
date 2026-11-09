package com.mezon.classmanagement.backend.domain_document.main.c_means.entity;

import com.mezon.classmanagement.backend.config.EmbeddingConfig;
import com.mezon.classmanagement.backend.domain_document.main.c_means.config.CMeansConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommendation_models")
public class RecommendationModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@Column(name = "embedding_model", nullable = false)
	String embeddingModel;

	@Column(name = "embedding_dimension", nullable = false)
	Integer embeddingDimension;

	@Column(name = "cluster_count", nullable = false)
	Integer clusterCount;

	@Column(name = "fuzziness", nullable = false)
	Double fuzziness;

	@Column(name = "min_iterations", nullable = false)
	Integer minIterations;

	@Column(name = "max_iterations", nullable = false)
	Integer maxIterations;

	@Column(name = "convergence_delta", nullable = false)
	Double convergenceDelta;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "text")
	Status status;

	public enum Status {
		TRAINING,
		READY,
		FAILED
	}

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	Instant createdAt;

	@Column(name = "trained_at", nullable = true)
	Instant trainedAt;

	@PrePersist
	public void prePersist() {
		if (embeddingModel == null) {
			embeddingModel = EmbeddingConfig.EMBEDDING_MODEL;
		}

		if (embeddingDimension == null) {
			embeddingDimension = EmbeddingConfig.EMBEDDING_DIMENSION;
		}

		if (fuzziness == null) {
			fuzziness = CMeansConfig.FUZZINESS;
		}

		if (minIterations == null) {
			minIterations = CMeansConfig.MIN_ITERATIONS;
		}

		if (maxIterations == null) {
			maxIterations = CMeansConfig.MAX_ITERATIONS;
		}

		if (convergenceDelta == null) {
			convergenceDelta = CMeansConfig.DELTA;
		}

		if (status == null) {
			status = Status.TRAINING;
		}
	}

	public static RecommendationModel create(Long id) {
		return RecommendationModel.builder()
				.id(id)
				.build();
	}

}