package com.mezon.classmanagement.backend.domain_document.main.document.entity;

import com.mezon.classmanagement.backend.common.constant.FileConstant;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector1536Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
		name = "documents",
		indexes = {
				@Index(
						name = "index_documents_title",
						columnList = "title"
				),
				@Index(
						name = "index_documents_file_name",
						columnList = "file_name"
				),
				@Index(
						name = "index_documents_file_extension",
						columnList = "file_extension"
				)
		}
)
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@Column(name = "key", nullable = false)
	String key;

	@Column(name = "title", nullable = false)
	String title;

	@Column(name = "description", nullable = true)
	String description;

	@Column(name = "is_image", nullable = false)
	Boolean isImage;

	@Column(name = "file_name", nullable = false)
	String fileName;

	@Column(name = "file_extension", nullable = false)
	String fileExtension;

	@JdbcTypeCode(value = SqlTypes.VECTOR)
	@Convert(converter = Vector1536Converter.class)
	@Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
	Vector1536 embedding;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_user_id", nullable = false)
	User creator;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	Instant createdAt;

}