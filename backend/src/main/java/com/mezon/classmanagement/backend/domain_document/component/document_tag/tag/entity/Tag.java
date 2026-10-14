package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mezon.classmanagement.backend.common.util.SlugGenerator;
import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector1536Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
		name = "tags",
		indexes = {
				@Index(
						name = "unique_index_tags_slug",
						columnList = "slug",
						unique = true
				)
		}
)
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	Long id;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "slug", nullable = false)
	String slug;


	@JdbcTypeCode(value = SqlTypes.VECTOR)
	@Convert(converter = Vector1536Converter.class)
	@Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
	Vector1536 embedding;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	Instant createdAt;

	@PrePersist
	public void prePersist() {
		if (embedding == null) {
			embedding = new Vector1536();
		}
		slug = SlugGenerator.generate(name);
	}

}