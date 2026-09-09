package com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity;

import com.mezon.classmanagement.backend.domain_document.component.vector.converter.impl.Vector3072Converter;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector3072;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentChunk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "document_id")
	private Long documentId;

	@Column(name = "index")
	private Short chunkIndex;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@JdbcTypeCode(SqlTypes.VECTOR)
	@Convert(converter = Vector3072Converter.class)
	@Column(name = "embedding", columnDefinition = "vector(3072)")
	private Vector3072 embedding;
}