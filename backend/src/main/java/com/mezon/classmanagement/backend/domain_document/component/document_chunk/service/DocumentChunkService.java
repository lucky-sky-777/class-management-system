package com.mezon.classmanagement.backend.domain_document.component.document_chunk.service;

import com.mezon.classmanagement.backend.domain_document.component.document_chunk.entity.DocumentChunk;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pgvector.PGvector;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentChunkService {

	private static final int BATCH_SIZE = 500;

	DataSource dataSource;
	JdbcTemplate jdbcTemplate;

	@Transactional
	public void insertBatch(List<DocumentChunk> chunks) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
				.withSchemaName("public")
				.withTableName("document_chunks")
				.usingColumns(
						"document_id",
						"index",
						"content",
						"embedding"
				);

		for (int start = 0; start < chunks.size(); start += BATCH_SIZE) {
			int end = Math.min(start + BATCH_SIZE, chunks.size());

			SqlParameterSource[] batch =
					new SqlParameterSource[end - start];

			for (int i = start; i < end; i++) {
				DocumentChunk chunk = chunks.get(i);

				batch[i - start] = new MapSqlParameterSource()
						.addValue(
								"document_id",
								chunk.getDocument().getId()
						)
						.addValue(
								"index",
								chunk.getIndex(),
								Types.SMALLINT
						)
						.addValue("content", chunk.getContent())
						.addValue(
								"embedding",
								new PGvector(chunk.getEmbedding().getValues())
						);
			}

			simpleJdbcInsert.executeBatch(batch);
		}
	}

}