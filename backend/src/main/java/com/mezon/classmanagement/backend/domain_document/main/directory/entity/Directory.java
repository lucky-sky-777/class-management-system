package com.mezon.classmanagement.backend.domain_document.main.directory.entity;

import com.mezon.classmanagement.backend.common.util.CodeGenerator;
import com.mezon.classmanagement.backend.common.util.DateTimeUtils;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector1536Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "directories",
		indexes = {
				@Index(
						name = "unique_index_directories_belong_to_user_id_name",
						columnList = "belong_to_user_id, name",
						unique = true,
						options = "where \"belong_to_user_id\" is not null"
				),
				@Index(
						name = "unique_index_directories_belong_to_class_id_name",
						columnList = "belong_to_class_id, name",
						unique = true,
						options = "where \"belong_to_class_id\" is not null"
				),
				@Index(
						name = "unique_index_directories_name_by_belong_to_user_id",
						columnList = "name",
						unique = true,
						options = "where \"belong_to_user_id\" is not null"
				),
				@Index(
						name = "unique_index_directories_name_by_belong_to_class_id",
						columnList = "name",
						unique = true,
						options = "where \"belong_to_class_id\" is not null"
				)
		}
)
public class Directory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "belong_to_user_id", nullable = true)
	User belongToUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "belong_to_class_id", nullable = true)
	Class belongToClass;

	@Column(name = "name", nullable = false)
	String name;

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
		REJECTED
	}

	@PrePersist
	public void prePersist() {
		if (status == null) {
			status = Status.PENDING;
		}
	}

}