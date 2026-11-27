package com.mezon.classmanagement.backend.domain_document.main.recommendation.calculator.dto;

import java.time.Instant;
import java.util.List;

public record ClusterInteraction(
		double behaviorWeight,
		Instant interactedAt,
		List<ClusterMembership> membership
) {
}