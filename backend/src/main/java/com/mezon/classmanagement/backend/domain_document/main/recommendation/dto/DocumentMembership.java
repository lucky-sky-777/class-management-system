package com.mezon.classmanagement.backend.domain_document.main.recommendation.dto;

public record DocumentMembership(
		Long documentId,
		Integer clusterIndex,
		Double membership
) {}