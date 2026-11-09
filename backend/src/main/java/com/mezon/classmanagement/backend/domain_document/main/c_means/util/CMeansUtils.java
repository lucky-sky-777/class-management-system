package com.mezon.classmanagement.backend.domain_document.main.c_means.util;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.config.EmbeddingConfig;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.FcmTrainingResult;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.FuzzyCMeansService;

import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
public final class CMeansUtils {

	/*
	 * Xie-Beni (XB) Index:
	 *
	 *                  C   N
	 *                  Σ   Σ (u_ij ^ m) * ||x_j - v_i||^2
	 *                 i=1 j=1
	 * XB(C) = ------------------------------------------------
	 *                 N * min ||v_i - v_k||^2
	 *                     i != k
	 *
	 * C     : số cụm đang thử nghiệm
	 * N     : số tài liệu
	 * u_ij  : mức độ thuộc cụm i của tài liệu thứ j
	 * m     : hệ số làm mờ, thường bắt đầu với m = 2
	 * x_j   : vector embedding của tài liệu thứ j
	 * v_i   : tâm cụm thứ i
	 * v_k   : Center of another cluster k (k != i)
	 */
	public static double calculateXieBeniIndex(
			List<double[]> embeddingList,
			double[][] membershipMatrix,
			List<double[]> centerList,
			double m
	) {
		int n = embeddingList.size(); // Số tài liệu
		int c = centerList.size();    // Số cụm

		if (n == 0) {
			throw new IllegalArgumentException("Danh sách embedding rỗng");
		}

		if (c < 2) {
			throw new IllegalArgumentException(
					"Cần ít nhất 2 cụm để tính Xie-Beni Index"
			);
		}

		if (m <= 1.0 || !Double.isFinite(m)) {
			throw new IllegalArgumentException("m phải lớn hơn 1");
		}

		if (membershipMatrix.length != n) {
			throw new IllegalArgumentException(
					"Số hàng membership phải bằng số tài liệu"
			);
		}

		int dimension = embeddingList.getFirst().length;

		// Kiểm tra kích thước dữ liệu
		validateEmbeddingList(embeddingList, dimension);

		for (double[] center : centerList) {
			if (center == null || center.length != dimension) {
				throw new IllegalArgumentException(
						"Tâm cụm không hợp lệ hoặc không đồng nhất kích thước"
				);
			}
		}

		for (double[] row : membershipMatrix) {
			if (row == null || row.length != c) {
				throw new IllegalArgumentException(
						"Mỗi tài liệu phải có membership cho tất cả cụm"
				);
			}
		}

		// Tử số: tổng u_ij^m * ||x_j - v_i||^2
		double numerator = 0.0;

		for (int j = 0; j < n; j++) {
			double[] documentVector = embeddingList.get(j);

			for (int i = 0; i < c; i++) {
				double u = membershipMatrix[j][i];

				if (!Double.isFinite(u) || u < 0.0 || u > 1.0) {
					throw new IllegalArgumentException(
							"Membership phải nằm trong [0, 1]"
					);
				}

				double distanceSquared = squaredEuclideanDistance(
						documentVector,
						centerList.get(i)
				);

				numerator += Math.pow(u, m) * distanceSquared;
			}
		}

		// Tìm khoảng cách bình phương nhỏ nhất giữa hai tâm cụm
		double minCenterDistanceSquared = Double.POSITIVE_INFINITY;

		for (int i = 0; i < c; i++) {
			for (int k = i + 1; k < c; k++) {
				double distanceSquared = squaredEuclideanDistance(
						centerList.get(i),
						centerList.get(k)
				);

				minCenterDistanceSquared = Math.min(
						minCenterDistanceSquared,
						distanceSquared
				);
			}
		}

		// Hai tâm trùng nhau khiến mẫu số bằng 0
		if (minCenterDistanceSquared == 0.0) {
			return Double.POSITIVE_INFINITY;
		}

		// XB = numerator / (N * khoảng cách tâm nhỏ nhất)
		return numerator / (n * minCenterDistanceSquared);
	}

	private static void validateEmbeddingList(List<double[]> embeddingList, int dimension) {
		for (double[] embedding : embeddingList) {
			if (embedding == null || embedding.length != dimension) {
				throw new IllegalArgumentException(
						"Embedding không hợp lệ hoặc không đồng nhất kích thước"
				);
			}
		}
	}

	private static double squaredEuclideanDistance(
			double[] a,
			double[] b
	) {
		if (a.length != b.length) {
			throw new IllegalArgumentException(
					"Hai vector phải có cùng số chiều"
			);
		}

		double sum = 0.0;

		for (int i = 0; i < a.length; i++) {
			double difference = a[i] - b[i];
			sum += difference * difference;
		}

		return sum;
	}

	public static void validateMembership(double[][] memberships, int clusterCount) {
		for (double[] membership : memberships) {
			if (membership == null || membership.length != clusterCount) {
				throw new IllegalStateException(
						"Ma trận membership không hợp lệ"
				);
			}
		}
	}

	public static void validateTrainingResult(FcmTrainingResult result, int documentCount) {
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