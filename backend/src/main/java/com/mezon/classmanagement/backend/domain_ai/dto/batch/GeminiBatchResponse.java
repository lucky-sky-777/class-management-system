package com.mezon.classmanagement.backend.domain_ai.dto.batch;



import com.mezon.classmanagement.backend.domain_ai.dto.EmbeddingData;

import java.util.List;

public record GeminiBatchResponse(List<EmbeddingData> embeddings) {}