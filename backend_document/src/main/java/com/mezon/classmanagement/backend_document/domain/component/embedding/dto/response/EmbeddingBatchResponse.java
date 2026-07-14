package com.mezon.classmanagement.backend_document.domain.component.embedding.dto.response;



import java.util.List;

public record EmbeddingBatchResponse(List<EmbeddingData> embeddings) {}