package com.mezon.classmanagement.backend.domain_document.main.c_means.util;

import com.mezon.classmanagement.backend.config.EmbeddingConfig;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.FuzzyCMeansService;

import java.util.List;

public final class CMeansUtils {

	public static void validateMembership(double[][] memberships, int clusterCount) {
		for (double[] membership : memberships) {
			if (membership == null || membership.length != clusterCount) {
				throw new IllegalStateException(
						"Ma trận membership không hợp lệ"
				);
			}
		}
	}

	public static void validateTrainingResult(FuzzyCMeansService.FcmTrainingResult result, int documentCount) {
		if (result.membershipMatrix().length != documentCount || result.dominantCluster().length != documentCount || result.centerList().isEmpty()) {
			throw new IllegalStateException(
					"Kết quả huấn luyện không khớp với dữ liệu đầu vào"
			);
		}
	}

	public static void validateCenter(double[] center, int clusterIndex) {
		if (center.length != EmbeddingConfig.EMBEDDING_DIMENSION) {
			throw new IllegalStateException(
					"Tâm cụm không đúng 1536 chiều: " + clusterIndex
			);
		}
	}

}