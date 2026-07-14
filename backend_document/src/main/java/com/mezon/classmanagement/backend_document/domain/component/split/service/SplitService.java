package com.mezon.classmanagement.backend_document.domain.component.split.service;

import com.mezon.classmanagement.backend_document.domain.component.split.strategy.SplitStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SplitService {

	Map<String, SplitStrategy> strategies;

	public DocumentSplitter getSplitter(SplitStrategy splitStrategy) {
		SplitStrategy strategy = strategies.getOrDefault(splitStrategy.getName(), null);

		if (strategy == null) {
			throw new RuntimeException("Splitter không được hỗ trợ");
		}

		return strategy.getSplitter();
	}

}