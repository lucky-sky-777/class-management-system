package com.mezon.classmanagement.backend.domain_document.component.embedding.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings(value = {WarningConstant.UNUSED, WarningConstant.SPELL_CHECKING_INSPECTION})
public class EmbeddingUtils {

	public static float[] toFloatArray(double[] vector) {
		float[] result = new float[vector.length];

		for (int i = 0; i < vector.length; i++) {
			result[i] = (float) vector[i];
		}

		return result;
	}

	public static double[] toDoubleArray(float[] vector) {
		double[] result = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			result[i] = vector[i];
		}

		return result;
	}

	public static List<double[]> toDoubleList(List<float[]> embeddings) {
		List<double[]> result = new ArrayList<>(embeddings.size());

		for (float[] embedding : embeddings) {
			result.add(toDoubleArray(embedding));
		}

		return result;
	}

	public static float cosinePercent(float cosine) {
		return cosine * 100.0f;
	}

	public static float cosinePercent(
			float[] a,
			float[] b
	) {
		return cosine(a, b) * 100.0f;
	}

	public static float cosineDistance(float cosine) {
		return 1.0f - cosine;
	}

	public static float cosineDistance(
			float[] a,
			float[] b
	) {
		return 1.0f - cosine(a, b);
	}

	public static float cosinePercentWithRound(float cosine) {
		float percent = cosine * 100.0f;
		return Math.round(percent * 100.0f) / 100.0f;
	}

	public static float cosinePercentWithRound(
			float[] a,
			float[] b
	) {
		float percent = cosine(a, b) * 100.0f;
		return Math.round(percent * 100.0f) / 100.0f;
	}

	public record parallel_com(
			double dotProduct,
			double normA,
			double normB
	) {
		// từ từ
	}

	@FunctionalInterface
	public interface FloatBinaryOperator {
		float applyAsFloat(
				float a,
				float b
		);
	}

	public static parallel_com parallel(
			float[] a,
			float[] b
	) {
		ensureLength(a, b);

		final double[] dotProduct = new double[1];
		final double[] normA = new double[1];
		final double[] normB = new double[2];

		IntStream
				.range(0, a.length)
				.parallel()
				.forEach(i -> {
					double x = a[i];
					double y = b[i];

					dotProduct[0] += x * y;
					normA[0] += x * x;
					normB[0] += y * y;
				});

		return new parallel_com(dotProduct[0], normA[0], normB[0]);
	}

	private static double simi(parallel_com com) {
		return com.dotProduct() / (Math.sqrt(com.normA()) * Math.sqrt(com.normB()));
	}

	public static float cosine(
			float[] a,
			float[] b
	) {
		ensureLength(a, b);

		parallel_com com = parallel(a, b);

		if (com.normA() == 0.0 || com.normB() == 0.0) {
			return 0.0f;
		}

		double similarity = simi(com);

		return (float) Math.clamp(similarity, -1.0, 1.0);
	}

	private static void ensureLength(
			float[] vectorA,
			float[] vectorB
	) {
		if (vectorA.length != vectorB.length) {
			throw new RuntimeException("vectorA.length != vectorB.length");
		}
	}

	/**
	 * Tính vector embedding của tài liệu từ danh sách embedding của các chunk có trọng số.
	 * @param embeddingList Danh sách các vector embedding (e_1, ..., e_n)
	 * @param weights Danh sách trọng số tương ứng (w_1, ..., w_n)
	 * @param applyL2Norm True nếu muốn chuẩn hóa L2 (normalize) kết quả
	 * @return Vector đại diện cho tài liệu (x_d)
	 */
	// x_d = normalize( sum(w_j * e_j) / sum(w_j) )
	public static float[] computeDocumentEmbedding(
			List<float[]> embeddingList,
			List<Double> weights,
			boolean applyL2Norm
	) {
		if (embeddingList == null || embeddingList.isEmpty()) {
			throw new IllegalArgumentException("Danh sách chunk embeddings không được rỗng.");
		}
		if (weights == null || embeddingList.size() != weights.size()) {
			throw new IllegalArgumentException("Số lượng trọng số phải khớp với số lượng chunk.");
		}

		int n = embeddingList.size();
		int dimension = embeddingList.get(0).length;
		double[] weightedSum = new double[dimension];
		double totalWeight = 0.0;

		for (int j = 0; j < n; j++) {
			float[] e_j = embeddingList.get(j);
			if (e_j.length != dimension) {
				throw new IllegalArgumentException("Tất cả vector embedding phải có cùng số chiều.");
			}
			double w_j = weights.get(j);
			totalWeight += w_j;

			for (int i = 0; i < dimension; i++) {
				weightedSum[i] += w_j * e_j[i];
			}
		}

		if (totalWeight == 0.0) {
			throw new ArithmeticException("Tổng trọng số bằng 0, không thể chia.");
		}

		// 2. Chia cho tổng trọng số để lấy vector trung bình
		float[] meanVector = new float[dimension];
		for (int i = 0; i < dimension; i++) {
			meanVector[i] = (float) (weightedSum[i] / totalWeight);
		}

		// 3. Chuẩn hóa L2 (nếu áp dụng)
		return applyL2Norm ? normalizeL2(meanVector) : meanVector;
	}

	/**
	 * Phiên bản rút gọn khi mọi chunk có trọng số bằng nhau (w_j = 1).
	 */
	public static float[] computeDocumentEmbeddingEqualWeights(
			List<float[]> chunkEmbeddings,
			boolean applyL2Norm) {

		List<Double> equalWeights = chunkEmbeddings.stream()
				.map(chunk -> 1.0)
				.toList();
		return computeDocumentEmbedding(chunkEmbeddings, equalWeights, applyL2Norm);
	}

	/**
	 * Hàm chuẩn hóa L2 đưa vector về độ dài chuẩn (norm = 1).
	 */
	public static float[] normalizeL2(float[] vector) {
		double sumSquares = 0.0;
		for (float v : vector) {
			double value = v;
			sumSquares += value * value;
		}

		double norm = Math.sqrt(sumSquares);
		if (norm == 0.0) {
			return vector;
		}

		float[] normalized = new float[vector.length];
		for (int i = 0; i < vector.length; i++) {
			double vector_i = vector[i];
			normalized[i] = (float) (vector_i / norm);
		}

		return normalized;
	}

	/**
	 * Tính trung bình có trọng số các chunk và chuẩn hóa L2:
	 * x_d = normalize( sum(w_j * e_j) / sum(w_j) )
	 */
	public static double[] computeWeightedNormalizedDocumentVector(
			List<double[]> chunkEmbeddings, List<Double> weights) {
		if (chunkEmbeddings == null || chunkEmbeddings.isEmpty()) {
			throw new IllegalArgumentException("Danh sách chunk embedding không được rỗng");
		}
		int dim = chunkEmbeddings.get(0).length;
		double[] sumVector = new double[dim];
		double totalWeight = 0.0;

		for (int j = 0; j < chunkEmbeddings.size(); j++) {
			double[] e_j = chunkEmbeddings.get(j);
			if (e_j.length != dim) {
				throw new IllegalArgumentException("Các vector không cùng số chiều D");
			}
			// Nếu không truyền trọng số, mặc định trọng số bằng nhau = 1.0
			double w_j = (weights != null && j < weights.size()) ? weights.get(j) : 1.0;
			totalWeight += w_j;
			for (int d = 0; d < dim; d++) {
				sumVector[d] += w_j * e_j[d];
			}
		}

		if (totalWeight > 0) {
			for (int d = 0; d < dim; d++) {
				sumVector[d] /= totalWeight;
			}
		}
		return normalizeL2(sumVector);
	}

	/**
	 * Chuẩn hóa L2 đưa vector về độ dài chuẩn
	 */
	public static double[] normalizeL2(double[] vector) {
		double norm = 0.0;
		for (double v : vector) {
			norm += v * v;
		}
		norm = Math.sqrt(norm);
		double[] result = new double[vector.length];
		if (norm > 1e-12) {
			for (int i = 0; i < vector.length; i++) {
				result[i] = vector[i] / norm;
			}
		}
		return result;
	}

	// ||x_i - v_k||
	public static double euclideanDistance(double[] a, double[] b) {
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			double diff = a[i] - b[i];
			sum += diff * diff;
		}
		return Math.sqrt(sum);
	}

	/**
	 * d(a, b) = sqrt(sigma(1, n) * pow(a_i - b_i))
	 * @param a vector a
	 * @param b vector b
	 */
	public static float euclideanDistance(float[] a, float[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("Vectors must have the same dimension");
		}

		double sum = 0.0;

		for (int i = 0; i < a.length; i++) {
			double diff = ((double) a[i]) - ((double) b[i]);
			sum += diff * diff;
		}

		return (float) Math.sqrt(sum);
	}

	/**
	 *
	 * @param distances
	 * @param k
	 * @param m
	 * @return
	 */
	public static float membership(List<Float> distances, int k, double m) {
		double sum = 0.0;

		for (int j = 0; j < distances.size(); j++) {
			if (distances.get(j) == 0.0f) {
				return j == k ? 1.0f : 0.0f;
			}

			double ratio = (double) distances.get(k) / (double) distances.get(j);
			sum += Math.pow(ratio, 2.0 / (m - 1.0));
		}

		return (float) (1.0 / sum);
	}

}