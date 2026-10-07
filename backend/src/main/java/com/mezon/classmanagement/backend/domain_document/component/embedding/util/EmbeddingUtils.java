package com.mezon.classmanagement.backend.domain_document.component.embedding.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.mezon.classmanagement.backend.common.constant.WarningConstant;

import java.util.stream.IntStream;

@SuppressWarnings(value = {WarningConstant.UNUSED})
public class EmbeddingUtils {

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

	private record parallel_com(
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

	public static float[] parallel(
			float[] a,
			float[] b,
			FloatBinaryOperator operation
	) {
		ensureLength(a, b);

		float[] result = new float[a.length];

		IntStream
				.range(0, a.length)
				.parallel().forEach(i -> result[i] = operation.applyAsFloat(a[i], b[i]));

		return result;
	}

	public static float cosine(
			float[] a,
			float[] b
	) {
		ensureLength(a, b);

		final double[] dotProduct = new double[1];
		final double[] normA = new double[1];
		final double[] normB = new double[2];

		AtomicDouble atomicDouble = new AtomicDouble(0.0d);

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

		if (normA[0] == 0.0 || normB[0] == 0.0) {
			return 0.0f;
		}

		double similarity =
				dotProduct[0] / (Math.sqrt(normA[0]) * Math.sqrt(normB[0]));

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

}