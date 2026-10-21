package com.mezon.classmanagement.backend.domain_document.main.c_means.dto;

import java.util.List;

public record DocumentEmbeddingList(
		List<Long> idList,
		List<float[]> embeddingList
) {
}