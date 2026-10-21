package com.mezon.classmanagement.backend.domain_document.main.c_means.service;

import com.mezon.classmanagement.backend.domain_document.main.c_means.config.CMeansConfig;
import elki.clustering.kmeans.FuzzyCMeans;
import elki.clustering.kmeans.initialization.RandomlyChosen;
import elki.data.Cluster;
import elki.data.Clustering;
import elki.data.NumberVector;
import elki.data.model.MeanModel;
import elki.data.type.TypeUtil;
import elki.database.Database;
import elki.database.StaticArrayDatabase;
import elki.database.ids.DBIDIter;
import elki.database.ids.DBIDRange;
import elki.database.relation.Relation;
import elki.datasource.ArrayAdapterDatabaseConnection;
import elki.datasource.DatabaseConnection;
import elki.result.Metadata;
import elki.utilities.datastructures.iterator.It;
import elki.utilities.random.RandomFactory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FuzzyCMeansService {

	public record FcmTrainingResult(
			List<double[]> centerList,
			double[][] membershipMatrix,
			int[] dominantCluster
	) {
	}

	public FcmTrainingResult train(List<double[]> documentEmbeddingList) {
		validate(documentEmbeddingList);

		int embeddingCount = documentEmbeddingList.size();
		int dimensions = documentEmbeddingList.getFirst().length;

		// Chuyển dữ liệu đầu vào thành ma trận double[][]
		double[][] dataMatrix = new double[embeddingCount][dimensions];

		for (int i = 0; i < embeddingCount; i++) {
			dataMatrix[i] = Arrays.copyOf(documentEmbeddingList.get(i), dimensions);
		}

		// Đưa dữ liệu vào database nội bộ của ELKI.
		DatabaseConnection connection =
				new ArrayAdapterDatabaseConnection(dataMatrix);

		Database database =
				new StaticArrayDatabase(connection, null);

		database.initialize();

		// Lấy relation chứa các embedding.
		Relation<NumberVector> vectorRelation =
				database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);

		// Khởi tạo FCM.
		FuzzyCMeans<NumberVector> fcm = new FuzzyCMeans<>(
				CMeansConfig.CLUSTER_COUNT,
				CMeansConfig.MIN_ITERATIONS,
				CMeansConfig.MAX_ITERATIONS,
				CMeansConfig.DELTA,
				CMeansConfig.FUZZINESS,
				true,
				new RandomlyChosen<NumberVector>(
						new RandomFactory(42L)
				)
		);

		// Huấn luyện.
		Clustering<MeanModel> clustering =
				fcm.run(vectorRelation);

		// Lấy ma trận membership từ kết quả ELKI.
		Relation<double[]> membershipRelation =
				findMembershipRelation(clustering);

		DBIDRange ids =
				(DBIDRange) vectorRelation.getDBIDs();

		double[][] memberships =
				new double[embeddingCount][CMeansConfig.CLUSTER_COUNT];

		// Ánh xạ membership về đúng thứ tự embedding đầu vào.
		for (DBIDIter it = vectorRelation.iterDBIDs();
		     it.valid();
		     it.advance()) {

			int row = ids.getOffset(it);
			memberships[row] =
					membershipRelation.get(it).clone();
		}

		// Lấy tâm cụm.
		List<double[]> centers = new ArrayList<>();

		for (Cluster<MeanModel> cluster
				: clustering.getToplevelClusters()) {

			centers.add(cluster.getModel().getMean().clone());
		}

		// Xác định cụm có membership cao nhất.
		int[] dominantCluster = new int[embeddingCount];

		for (int i = 0; i < embeddingCount; i++) {
			int best = 0;

			for (int j = 1; j < CMeansConfig.CLUSTER_COUNT; j++) {
				if (memberships[i][j] > memberships[i][best]) {
					best = j;
				}
			}

			dominantCluster[i] = best;
		}

		return new FcmTrainingResult(
				List.copyOf(centers),
				memberships,
				dominantCluster
		);
	}

	@SuppressWarnings("unchecked")
	private Relation<double[]> findMembershipRelation(
			Clustering<MeanModel> clustering
	) {

		It<Object> children =
				Metadata.hierarchyOf(clustering).iterChildren();

		while (children.valid()) {
			Object child = children.get();

			if (child instanceof Relation<?> relation
					&& "FCM Cluster Probabilities"
					.equals(relation.getLongName())) {

				return (Relation<double[]>) relation;
			}

			children.advance();
		}

		throw new IllegalStateException(
				"ELKI không trả về ma trận membership."
		);
	}

	private void validate(List<double[]> input) {
		if (input == null
				|| input.size() < CMeansConfig.CLUSTER_COUNT) {
			throw new IllegalArgumentException(
					"Số embedding phải lớn hơn hoặc bằng số cụm."
			);
		}

		if (input.get(0) == null
				|| input.get(0).length == 0) {
			throw new IllegalArgumentException(
					"Embedding không được rỗng."
			);
		}

		int dimensions = input.get(0).length;

		for (double[] vector : input) {
			if (vector == null
					|| vector.length != dimensions) {
				throw new IllegalArgumentException(
						"Các embedding phải cùng số chiều."
				);
			}

			for (double value : vector) {
				if (!Double.isFinite(value)) {
					throw new IllegalArgumentException(
							"Embedding chứa giá trị không hợp lệ."
					);
				}
			}
		}
	}
}