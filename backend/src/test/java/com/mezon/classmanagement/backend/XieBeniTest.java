package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.component.embedding.util.EmbeddingUtils;
import com.mezon.classmanagement.backend.domain_document.main.c_means.config.CMeansConfig;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.DocumentEmbeddingList;
import com.mezon.classmanagement.backend.domain_document.main.c_means.dto.FcmTrainingResult;
import com.mezon.classmanagement.backend.domain_document.main.c_means.service.FuzzyCMeansService;
import com.mezon.classmanagement.backend.domain_document.main.c_means.util.CMeansUtils;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class XieBeniTest {

	DocumentService documentService;
	FuzzyCMeansService fuzzyCMeansService;

	private record CAndXieBeni (int c, double xieBeni) {}

	@Test
	public void test() {
		// cần ít nhất 2 cụm để tính Xie-Beni
		List<Integer> cList = List.of(
				/*1, */2, 3, 4, 5, 6, 7, 8, 9, 10
		);
		List<CAndXieBeni> xieBeniList = new ArrayList<>();
		DocumentEmbeddingList documentEmbeddingList = documentService.getEmbeddingList();
		List<double[]> embeddingList = EmbeddingUtils.toDoubleList(
				documentEmbeddingList.embeddingList()
		);

		for (Integer c : cList) {
			FcmTrainingResult result = fuzzyCMeansService
					.train(embeddingList, c);

			xieBeniList.add(
					new CAndXieBeni(
							c,
							CMeansUtils.calculateXieBeniIndex(
									embeddingList,
									result.membershipMatrix(),
									result.centerList(),
									CMeansConfig.FUZZINESS
							)
					)
			);
		}

		for (CAndXieBeni item : xieBeniList) {
			System.out.println("c: " + item.c() + ", xie-beni: " + item.xieBeni());
		}
	}

}