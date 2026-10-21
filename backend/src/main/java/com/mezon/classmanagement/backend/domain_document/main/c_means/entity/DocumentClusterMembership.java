package com.mezon.classmanagement.backend.domain_document.main.c_means.entity;

import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document_cluster_memberships")
public class DocumentClusterMembership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	Document documentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster_id", nullable = false)
	RecommendationCluster clusterId;

	@Column(name = "membership", nullable = false)
	Double membership;

	public static DocumentClusterMembership create(Long id) {
		return DocumentClusterMembership.builder()
				.id(id)
				.build();
	}

}