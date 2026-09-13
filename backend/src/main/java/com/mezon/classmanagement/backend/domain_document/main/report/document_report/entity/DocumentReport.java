package com.mezon.classmanagement.backend.domain_document.main.report.document_report.entity;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.report.report_category.entity.ReportCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(
		name = "document_reports"
)
public class DocumentReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	Document document;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_category_id", nullable = true)
	ReportCategory reportCategory;

	@Column(name = "description", nullable = true)
	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_user_id", nullable = true)
	User creator;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	Instant createdAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	Status status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actor_user_id", nullable = true)
	User actor;

	@Column(name = "acted_at", nullable = true)
	Instant actedAt;

	public enum Status {
		PENDING,
		APPROVED,
		REJECTED,
		CANCELLED
	}

	@PrePersist
	public void prePersist() {
		if (status == null) {
			status = Status.PENDING;
		}
	}

}