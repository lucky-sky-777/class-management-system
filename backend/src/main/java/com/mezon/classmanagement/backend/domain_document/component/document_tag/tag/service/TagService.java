package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.SlugGenerator;
import com.mezon.classmanagement.backend.domain_document.component.chunk.service.ChunkService;
import com.mezon.classmanagement.backend.domain_document.component.chunk.strategy.impl.ChunkByParagraphWithoutOverlapStrategy;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_source.impl.MultipartFileDocumentSource;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.entity.DocumentTag;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.repository.DocumentTagRepository;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.entity.Tag;
import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.repository.TagRepository;
import com.mezon.classmanagement.backend.domain_document.component.nlp.analyzer.VnCoreNLPAnalyzer;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Propagation;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TagService {

	ChunkService chunkService;
	ChunkByParagraphWithoutOverlapStrategy chunkByParagraphWithoutOverlapStrategy;

	VnCoreNLPAnalyzer vnCoreNLPAnalyzer;
	StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

	/**
	 * Code mới viết từ đây
	 */

	TagRepository  tagRepository;
	DocumentTagRepository documentTagRepository;
	DocumentRepository  documentRepository;

	public Tag create(String name) {
		if (name == null || name.isBlank()) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST,"Tag name is blank");
		}

		String slug = SlugGenerator.generate(name.trim());
		if (slug.isEmpty()) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Invalid tag name");
		}

		if(tagRepository.existsBySlug(slug)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Tag already exists");
		}

		return tagRepository.saveAndFlush(Tag.builder().name(name.trim()).slug(slug).build());
	}


	public void delete(Long tagId) {
		if(!tagRepository.existsById(tagId)) {
			throw new GlobalException(GlobalException.Type.NOT_EXISTS,"Tag not found");
		}

		tagRepository.deleteById(tagId);
	}

	public List<Tag> getTagsOfDocument(Long documentId) {
		return documentTagRepository.findByDocument_Id(documentId)
				.map(documentTag -> {
					Long[] tagIds = documentTag.getTagIds();
					if (tagIds == null || tagIds.length == 0) {
						return List.<Tag>of();
					}
					return tagRepository.findByIdIn(List.of(tagIds));
				})
				.orElse(List.of());
	}

	@Transactional
	public void replaceTags(Long documentId, Long[] tagIds) {
		if(!documentRepository.existsById(documentId)) {
			throw new GlobalException(GlobalException.Type.NOT_EXISTS,"Document not found");
		}

		Long[] safeTagIds = sanitize(tagIds);
		validateTagIdExist(safeTagIds);

		DocumentTag documentTag = documentTagRepository
				.findByDocument_Id(documentId)
				.orElseGet(() -> DocumentTag.builder()
						.document(documentRepository.getReferenceById(documentId))
						.build());

		documentTag.setTagIds(safeTagIds);
		documentTag.setUpdatedAt(Instant.now());

		documentTagRepository.save(documentTag);
	}

	//loại bỏ null + trùng
	private Long[] sanitize(Long[] tagIds) {
		if(tagIds == null) {
			return new  Long[0];
		}

		return Arrays.stream(tagIds)
				.filter(Objects::nonNull)
				.distinct()
				.toArray(Long[]::new);
	}

	// id tồn tại
	private void validateTagIdExist(Long[] tagIds) {
		if(tagIds.length == 0) {
			return;
		}
		if (tagRepository.findByIdIn(List.of(tagIds)).size() != tagIds.length) {
			throw new GlobalException(GlobalException.Type.NOT_EXISTS, "Some tags do not exist");
		}

	}
	@Transactional
	public Long[] findOrCreateIds(List<String> tagNames) {

		if (tagNames == null || tagNames.isEmpty()) {
			return new Long[0];
		}

		Map<String, Long> slugToId = new LinkedHashMap<>();

		for (String name : tagNames) {

			if (name == null || name.isBlank()) {
				continue;
			}

			String slug = SlugGenerator.generate(name.trim());
			if (slug.isEmpty()) {
				continue;
			}

			// đã xử lý slug này rồi -> bỏ qua
			if (slugToId.containsKey(slug)) {
				continue;
			}

			slugToId.put(slug, findOrCreateIdBySlug(name, slug));
		}

		return slugToId.values().stream()
				.distinct()
				.toArray(Long[]::new);
	}


	// khóa slug trước khi check-and-insert -> chống race condition giữa các thread
	private Long findOrCreateIdBySlug(String name, String slug) {
		tagRepository.lockSlug(slug);

		Optional<Tag> existing = tagRepository.findBySlug(slug);
		if (existing.isPresent()) {
			return existing.get().getId();
		}

		return create(name).getId();
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void autoTag(Long documentId, List<String> chunkList) throws Exception {
		Document document = documentRepository
				.findById(documentId)
				.orElse(null);

		if (document == null) {
			System.out.println("Skip auto-tag. Document not found: " + documentId);
			return;
		}

		List<String> tagsName = extractTagsWithVnCoreNLP(chunkList, 20);

		Long[] tags = findOrCreateIds(tagsName);

		replaceTags(document.getId(), tags);
	}

	/**
	 * Code mới viết đến đây
	 */

	@Deprecated
	public List<String> extractTagsWithStandard(
			String content,
			int topK
	) throws Exception {
		return getTagList(content, topK, standardAnalyzer);
	}

	@Deprecated
	public List<String> extractTagsWithVnCoreNLP(
			String content,
			int topK
	) throws Exception {
		return getTagList(content, topK, vnCoreNLPAnalyzer);
	}

	public List<String> extractTagsWithVnCoreNLP(
			MultipartFile multipartFile,
			int topK
	) throws Exception {
		DocumentSource documentSource = new MultipartFileDocumentSource(multipartFile);

		List<String> chunkList = chunkService
				.getChunkList(
						chunkByParagraphWithoutOverlapStrategy,
						documentSource
				);

		return getTagListFromChunkList(chunkList, topK, vnCoreNLPAnalyzer);
	}

	public List<String> extractTagsWithVnCoreNLP(
			List<String> chunkList,
			int topK
	) throws Exception {
		return getTagListFromChunkList(chunkList, topK, vnCoreNLPAnalyzer);
	}

	private List<String> getTagList(
			String content,
			int topK,
			Analyzer analyzer
	) throws Exception {
		Map<String, Integer> termFrequencies = new HashMap<>();

		getTermFrequencyMap(content, analyzer, termFrequencies);

		return getTopTagList(termFrequencies, topK);
	}

	private List<String> getTagListFromChunkList(
			List<String> chunkList,
			int topK,
			Analyzer analyzer
	) throws IOException {
		Map<String, Integer> termFrequencies = new HashMap<>();

		for (String chunk : chunkList) {
			if (chunk == null || chunk.isBlank()) {
				continue;
			}

			getTermFrequencyMap(chunk, analyzer, termFrequencies);
		}

		return getTopTagList(termFrequencies, topK);
	}

	private List<String> getTopTagList(
			Map<String, Integer> termFrequencies,
			int topK
	) {
		return termFrequencies.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.limit(topK)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private void getTermFrequencyMap(
			String content,
			Analyzer analyzer,
			Map<String, Integer> termFrequencyMap
	) throws IOException {
		try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content))) {
			CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();

			while (tokenStream.incrementToken()) {
				String term = charTermAttr.toString().trim();

				if (term.length() > 2 && !term.chars().allMatch(Character::isDigit)) {
					termFrequencyMap.merge(term, 1, Integer::sum);
				}

				/*
				noinspection
				if (term.length() > 2 && !term.matches("\\d+")) {
					termFrequencyMap.put(term, termFrequencyMap.getOrDefault(term, 0) + 1);
				}
				*/
			}

			tokenStream.end();
		}
	}

}