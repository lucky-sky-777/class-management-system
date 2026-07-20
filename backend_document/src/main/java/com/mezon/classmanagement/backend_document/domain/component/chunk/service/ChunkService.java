package com.mezon.classmanagement.backend_document.domain.component.chunk.service;

import com.mezon.classmanagement.backend_document.common.util.FileUtils;
import com.mezon.classmanagement.backend_document.common.util.GeminiPromptBuilder;
import com.mezon.classmanagement.backend_document.domain.component.split.service.SplitService;
import com.mezon.classmanagement.backend_document.domain.component.split.strategy.SplitStrategy;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ChunkService {

	SplitService splitService;

	public List<String> getChunkListFromFile(
			File file,
			SplitStrategy splitStrategy
	) {
		try {
			Document document = FileUtils.toDocument(file);

			return formatChunkListForGemini(
					document.metadata(),
					getChunkList(document, splitStrategy)
			);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<String> getChunkListFromMultipartFile(
			MultipartFile multipartFile,
			SplitStrategy splitStrategy
	) {
		try {
			Document document = FileUtils.toDocument(multipartFile);

			return formatChunkListForGemini(
					document.metadata(),
					getChunkList(document, splitStrategy)
			);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<String> getChunkListFromFilePath(
			String filePath,
			SplitStrategy splitStrategy
	) {
		try {
			Document document = FileUtils.toDocument(filePath);

			return formatChunkListForGemini(
					document.metadata(),
					getChunkList(document, splitStrategy)
			);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private List<String> getChunkList(
			Document document,
			SplitStrategy splitStrategy
	) {
		List<String> chunkList = new ArrayList<>();

		List<TextSegment> segments = splitService
				.getSplitter(splitStrategy)
				.split(document);

		for (TextSegment segment : segments) {
			chunkList.add(
					formatTextSegment(segment)
			);
		}

		return chunkList;
	}

	private String formatTextSegment(TextSegment textSegment) {
		return textSegment.text().trim().replaceAll("\\s+", " ");
	}

	private List<String> formatChunkListForGemini(
			Metadata metadata,
			List<String> chunkList
	) {
		List<String> formattedChunkListForGemini = new ArrayList<>();

		String fileName = FileUtils.getFileNameFromMetadata(metadata);

		int chunkIndex = 1;
		for (String chunk : chunkList) {
			String chunkTitle = String.format("%s (Phần %d)", fileName, chunkIndex);
			String formattedChunk = GeminiPromptBuilder.buildDocumentPrompt(chunkTitle, chunk);

			formattedChunkListForGemini.add(formattedChunk);
			chunkIndex++;
		}

		return formattedChunkListForGemini;
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