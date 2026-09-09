package com.mezon.classmanagement.backend.domain_document.component.embedding.dto.response;

import java.util.List;

public record EmbeddingBatchResponse(List<EmbeddingData> embeddings) {}