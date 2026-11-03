package com.mezon.classmanagement.backend.domain_document.main.recommendation.dto;

import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;

public record MembershipOfUserWithCluster(
		Vector1536 userEmbedding,
		Integer clusterIndex,
		Float membership
) {
}