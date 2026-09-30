package com.mezon.classmanagement.backend.domain_document.component.chunk.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain_document.component.chunk.builder.ChunkListBuilder;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.ChunkStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.FileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.service.SplitService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl.DocumentSplitterByParagraphWithOverlapStrategy;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ChunkService {

	Map<String, ChunkStrategy> chunkStrategyMap;

	public List<String> getChunkList(
			ChunkStrategy chunkStrategy,
			DocumentSource documentSource
	) {
		ChunkStrategy strategy = chunkStrategyMap.getOrDefault(chunkStrategy.getName(), null);

		if (strategy == null) {
			throw new RuntimeException("ChunkStrategy không được hỗ trợ");
		}

		try {
			return strategy.getChunkList(documentSource);
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	SplitService splitService;

	public List<String> getChunkListFromFile(
			File file,
			SplitStrategy splitStrategy
	) {
		try {
			return new ChunkListBuilder(splitService)
					.from(new FileDocumentSource(file))
					.splitStrategy(splitStrategy)
					.forGemini()
					.build();
		} catch (Exception e) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR);
		}
	}

	public List<String> getChunkListFromMultipartFile(
			MultipartFile multipartFile,
			SplitStrategy splitStrategy
	) {
		try {
			Document document = FileUtils.toDocument(multipartFile);

			return ChunkListBuilder.formatChunkListForGemini(
					document.metadata(),
					getChunkList(document, splitStrategy)
			);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	DocumentSplitterByParagraphWithOverlapStrategy documentSplitterByParagraphWithOverlapStrategy;

	private List<String> getChunkList(
			Document document,
			SplitStrategy splitStrategy
	) {
		List<String> chunkList = new ArrayList<>();

		List<TextSegment> segments = documentSplitterByParagraphWithOverlapStrategy.getSplitter()
				.split(document);

		for (TextSegment segment : segments) {
			chunkList.add(
					ChunkListBuilder.formatTextSegment(segment)
			);
		}

		return chunkList;
	}


	@Deprecated
	public List<String> getChunkListFromFilePath(
			String filePath,
			DocumentSplitter documentSplitter
	) {
		try {
			FileInputStream fis = new FileInputStream(filePath);

			XWPFDocument document = new XWPFDocument(fis);
			XWPFWordExtractor wordExtractor = new XWPFWordExtractor(document);
			POIXMLProperties.CoreProperties props =
					document.getProperties().getCoreProperties();

			HWPFDocument document1 = new HWPFDocument(fis);
			WordExtractor wordExtractor1 = new WordExtractor(document1);

			System.out.println(props.getCreator());
			System.out.println(props.getLastModifiedByUser());

			for (XWPFParagraph paragraph : document.getParagraphs()) {
				System.out.println(" - " + paragraph.getText());
			}

			document.close();
		} catch (Exception ignored) {
		}
		return null;
	}

}