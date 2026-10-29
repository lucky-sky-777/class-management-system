package com.mezon.classmanagement.backend.domain_document.main.c_means.dto;

import java.util.List;

public record FcmTrainingResult(
		List<double[]> centerList,
		double[][] membershipMatrix,
		int[] dominantCluster
) {
}