package com.mezon.classmanagement.backend.domain_document.component.chunk.builder;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.common.util.GeminiPromptBuilder;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.split.DocumentSplitterService;
import com.mezon.classmanagement.backend.domain_document.component.split.service.SplitService;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.SplitStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.DocumentSplitterStrategy;
import com.mezon.classmanagement.backend.domain_document.component.split.strategy.document_splitter.impl.DocumentSplitterByParagraphWithOverlapStrategy;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChunkListBuilder {

	private SplitService splitService;
	private DocumentSplitterService documentSplitterService;
	private DocumentSplitter documentSplitter;

	private Document document;
	private SplitStrategy splitStrategy;
	private DocumentSplitterStrategy documentSplitterStrategy;
	private boolean forGemini = false;

	public ChunkListBuilder() {
	}

	public ChunkListBuilder(SplitService splitService) {
		this.splitService = splitService;
	}

	public ChunkListBuilder from(DocumentSource documentSource) throws Exception {
		this.document = documentSource.toDocument();
		return this;
	}

	public ChunkListBuilder splitStrategy(SplitStrategy splitStrategy) {
		this.splitStrategy = splitStrategy;
		return this;
	}

	public ChunkListBuilder documentSplitter(DocumentSplitter documentSplitter) {
		this.documentSplitter = documentSplitter;
		return this;
	}

	public ChunkListBuilder forGemini() {
		this.forGemini = true;
		return this;
	}

	public List<String> build() {

//		if (forGemini) {
//			return formatChunkListForGemini(
//					document.metadata(),
//					chunkList
//			);
//		}

		return getChunkList(document);
	}

	public static List<String> getChunkList(
			Document document,
			DocumentSplitter documentSplitter
	) {
		List<TextSegment> textSegmentList = documentSplitter
				.split(document);

		List<String> chunkList = new ArrayList<>(textSegmentList.size());

		for (TextSegment textSegment : textSegmentList) {
			chunkList.add(
					formatTextSegment(textSegment)
			);
		}

		return chunkList;
	}

	private List<String> getChunkList(
			Document document
	) {
		List<TextSegment> segments = documentSplitter
				.split(document);
		List<String> chunkList = new ArrayList<>(segments.size());

		for (TextSegment segment : segments) {
			chunkList.add(
					formatTextSegment(segment)
			);
		}

		return chunkList;
	}

	public static String formatTextSegment(TextSegment textSegment) {
		return textSegment.text().trim().replaceAll("\\s+", " ");
	}

	public static String formatChunkForGemini(
			String chunk,
			Metadata metadata
	) {
		String fileName = metadata.getString("fileName");
		Integer chunkIndex = metadata.getInteger("chunkIndex");
		String chunkTitle = String.format("%s (Phần %d)", fileName, chunkIndex);
		return GeminiPromptBuilder.buildDocumentPrompt(chunkTitle, chunk);
	}

	public static List<String> formatChunkListForGemini(
			Metadata metadata,
			List<String> chunkList
	) {
		List<String> formattedChunkListForGemini = new ArrayList<>();

		String fileName = FileUtils.getFileNameFromMetadata(metadata);

		int chunkIndex = 1;
		for (String chunk : chunkList) {
			formattedChunkListForGemini.add(
					formatChunkForGemini(
							chunk,
							metadata
									.put("fileName", fileName)
									.put("chunkIndex", chunkIndex++)
					)
			);
		}

		return formattedChunkListForGemini;
	}

}