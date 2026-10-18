package com.mezon.classmanagement.backend.domain_document.component.split.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SplitService {

	Map<String, SplitStrategy> splitStrategyMap;

	public List<String> getChunkList(SplitStrategy splitStrategy, DocumentSource documentSource) {
		SplitStrategy strategy = splitStrategyMap.getOrDefault(splitStrategy.getName(), null);

		if (strategy == null) {
			throw new RuntimeException("SplitStrategy không được hỗ trợ");
		}

		try {
			return strategy.getTextList(documentSource);
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}