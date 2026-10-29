package com.mezon.classmanagement.backend.domain_document.main.c_means.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.main.c_means.config.CMeansConfig;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.FcmTrainingResult;
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

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@Service
public class FuzzyCMeansService {

	/**
	 * Huấn luyện mô hình phân cụm
	 *
	 * @param embeddingList danh sách các vector cần phân cụm
	 * @return {@code FcmTrainingResult} - record kết quả phân cụm
	 */
	public FcmTrainingResult train(List<double[]> embeddingList) {
		validate(embeddingList);

		/*
		 * [0.1, 0.5, 0.9]
		 * [0.3, 0.5, 0.7]
		 *
		 * embeddingCount = row = 2
		 * dimensionCount = col = 3
		 *
		 * mỗi hàng là 1 vector đại diện cho 1 đối tượng cần phân cụm
		 * ví dụ: 1 tài liệu, 1 nhóm học,...
		 */
		//int embeddingCount = embeddingList.size();

		/*
		 * chuyển danh sách vector sang dạng ma trận
		 * kích thước: embeddingCount x dimensionCount
		 */
		double[][] embeddingMatrix = convertToEmbeddingMatrix(embeddingList);

		/*
		 * nạp ma trận vào database ELKI
		 */
		DatabaseConnection databaseConnection =
				new ArrayAdapterDatabaseConnection(embeddingMatrix);
		Database database =
				new StaticArrayDatabase(databaseConnection, null);
		database.initialize();

		/*
		 * trích xuất danh sách các vector từ database EKLI
		 */
		Relation<NumberVector> vectorRelation =
				database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);

		/*
		 * khởi tạo mô hình Fuzzy C-Means
		 */
		FuzzyCMeans<NumberVector> fcm = new FuzzyCMeans<>(
				CMeansConfig.CLUSTER_COUNT,
				CMeansConfig.MIN_ITERATIONS,
				CMeansConfig.MAX_ITERATIONS,
				CMeansConfig.DELTA,
				CMeansConfig.FUZZINESS,
				CMeansConfig.SOFT,
				new RandomlyChosen<NumberVector>(
						new RandomFactory(CMeansConfig.SEED)
				)
		);

		/*
		 * chạy C-Means trên danh sách vector và trả về đối tượng chứa kết quả phân cụm
		 */
		Clustering<MeanModel> clustering = fcm.run(vectorRelation);

		/*
		 * trích xuất danh sách các mảng membership từ kết quả phân cụm
		 * mỗi mảng membership thể hiện mức độ liên quan của mỗi vector với mỗi cụm
		 */
		Relation<double[]> membershipRelation = findMembershipRelation(clustering);

		/*
		 * lấy danh sách id từ danh sách các vector
		 *
		 * ví dụ:
		 * id | vector
		 *  0 | vector_0
		 *  1 | vector_1
		 *  2 | vector_2
		 * ...| ...
		 *  n | vector_n
		 *
		 * => ids = [0, 1, 2, ..., n]
		 */
		DBIDRange ids = (DBIDRange) vectorRelation.getDBIDs();

		/*
		 * khởi tạo ma trận membership
		 *
		 * ví dụ: có 4 vector và 3 cụm, ma trận membership sẽ có dạng như sau:
		 *
		 * cho i = embeddingCount
		 * cho j = CMeansConfig.CLUSTER_COUNT
		 *
		 * U = [
		 *  [0.80, 0.15, 0.05]
		 *  [0.25, 0.60, 0.15]
		 *  [0.10, 0.20, 0.70]
		 *  [0.45, 0.40, 0.15]
		 * ] = i x j = 4 x 3
		 *
		 * - mỗi hàng tương ứng với một vector
		 * - mỗi cột tương ứng với một cụm
		 * - tổng các giá trị trên mỗi hàng bằng 1
		 * - giá trị U_i_j biểu thị mức độ vector i thuộc về cụm j
		 */
		double[][] membershipMatrix = initializeMembershipMatrix(embeddingList.size(), vectorRelation, clustering);

		/*
		 * khởi tạo danh sách tâm cụm
		 */
		List<double[]> centerList = new ArrayList<>();

		/*
		 * sao chép tâm cụm từ kết quả phân cụm vào danh sách tâm cụm
		 */
		for (Cluster<MeanModel> cluster : clustering.getToplevelClusters()) {
			centerList.add(cluster.getModel().getMean().clone());
		}

		/*
		 * tìm danh sách cụm vượt trội (lưu theo số thứ tự của cụm)
		 * nghĩa là mỗi vector sẽ có một cụm mà vector đó thuộc về nhiều nhất
		 *
		 * ví dụ:
		 *
		 * U = [
		 *  [0.80, 0.15, 0.05]
		 *  [0.25, 0.60, 0.15]
		 *  [0.10, 0.20, 0.70]
		 *  [0.45, 0.40, 0.15]
		 * ]
		 *
		 * dominantCluster = [1, 2, 3, 1]
		 */
		int[] dominantCluster = findDominantClusters(membershipMatrix);

		return new FcmTrainingResult(
				List.copyOf(centerList),
				membershipMatrix,
				dominantCluster
		);
	}

	private double[][] initializeMembershipMatrix(
			int embeddingCount,
			Relation<NumberVector> vectorRelation,
			Clustering<MeanModel> clustering

	) {
		double[][] membershipMatrix = new double[embeddingCount][CMeansConfig.CLUSTER_COUNT];

		/*
		 * trích xuất danh sách các mảng membership từ kết quả phân cụm
		 * mỗi mảng membership thể hiện mức độ liên quan của mỗi vector với mỗi cụm
		 */
		Relation<double[]> membershipRelation = findMembershipRelation(clustering);

		/*
		 * lấy danh sách id từ danh sách các vector
		 *
		 * ví dụ:
		 * id | vector
		 *  0 | vector_0
		 *  1 | vector_1
		 *  2 | vector_2
		 * ...| ...
		 *  n | vector_n
		 *
		 * => ids = [0, 1, 2, ..., n]
		 */
		DBIDRange ids = (DBIDRange) vectorRelation.getDBIDs();

		for (DBIDIter it = vectorRelation.iterDBIDs(); it.valid(); it.advance()) {
			int row = ids.getOffset(it);
			membershipMatrix[row] = membershipRelation.get(it).clone();
		}

		return membershipMatrix;
	}

	/**
	 * chuyển đổi danh sách vector sang dạng ma trận
	 *
	 * @param embeddingList danh sách vector cần chuyển đổi
	 * @return {@code double[][]} ma trận vector
	 */
	private double[][] convertToEmbeddingMatrix(List<double[]> embeddingList) {
		int embeddingCount = embeddingList.size();
		int dimensionCount = embeddingList.getFirst().length;

		double[][] embeddingMatrix = new double[embeddingCount][dimensionCount];

		for (int i = 0; i < embeddingCount; i++) {
			embeddingMatrix[i] = Arrays.stream(embeddingList.get(i)).toArray();
		}
		return embeddingMatrix;
	}

	/**
	 * Tìm danh sách cụm vượt trội
	 *
	 * @param membershipMatrix ma trận membership
	 * @return {@code int[]} danh sách cụm vượt trội
	 */
	private int[] findDominantClusters(
			double[][] membershipMatrix
	) {
		int[] result = new int[membershipMatrix.length];

		for (int i = 0; i < result.length; i++) {
			int best = 0;

			for (int j = 1; j < CMeansConfig.CLUSTER_COUNT; j++) {
				if (membershipMatrix[i][j] > membershipMatrix[i][best]) {
					best = j;
				}
			}

			result[i] = best;
		}

		return result;
	}

	/**
	 * Tìm danh sách membership từ đối tượng lưu kết quả phân cụm
	 *
	 * @param clustering kết quả phân cụm
	 * @return {@code Relation<double[]>} - danh sách membership
	 */
	@SuppressWarnings("unchecked")
	private Relation<double[]> findMembershipRelation(
			Clustering<MeanModel> clustering
	) {
		It<Object> children = Metadata
				.hierarchyOf(clustering)
				.iterChildren();

		while (children.valid()) {
			Object child = children.get();

			if (child instanceof Relation<?> relation && "FCM Cluster Probabilities".equals(relation.getLongName())) {

				return (Relation<double[]>) relation;
			}

			children.advance();
		}

		throw new IllegalStateException(
				"ELKI không trả về ma trận membership."
		);
	}

	private void validate(List<double[]> input) {
		if (input == null || input.size() < CMeansConfig.CLUSTER_COUNT) {
			throw new IllegalArgumentException(
					"Số embedding phải lớn hơn hoặc bằng số cụm."
			);
		}

		if (input.getFirst() == null || input.getFirst().length == 0) {
			throw new IllegalArgumentException(
					"Embedding không được rỗng."
			);
		}

		int dimensions = input.getFirst().length;

		for (double[] vector : input) {
			if (vector == null || vector.length != dimensions) {
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