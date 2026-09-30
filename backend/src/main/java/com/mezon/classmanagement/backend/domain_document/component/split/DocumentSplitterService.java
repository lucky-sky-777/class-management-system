package com.mezon.classmanagement.backend.domain_document.component.split;

import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.DocumentSplitterStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentSplitterService {

	Map<String, DocumentSplitterStrategy> strategies;

	public DocumentSplitter getSplitter(DocumentSplitterStrategy documentSplitterStrategy) {
		DocumentSplitterStrategy strategy = strategies.getOrDefault(documentSplitterStrategy.getName(), null);

		if (strategy == null) {
			throw new RuntimeException("SplitStrategy không được hỗ trợ");
		}

		return strategy.getSplitter();
	}

}