package com.mezon.classmanagement.backend.domain_document.main.directory.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.common.util.SlugGenerator;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.DirectoryResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.directory.mapper.DirectoryMapper;
import com.mezon.classmanagement.backend.domain_document.main.directory.repository.DirectoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DirectoryService {

	DirectoryRepository directoryRepository;

	DirectoryMapper directoryMapper;

	ApplicationEventPublisher applicationEventPublisher;

	public record DirectoryCreatedEvent(Long classId, String directorySlug) {
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	@Component
	public static class DirectoryEventListener {

		S3Service s3Service;

		@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
		public void handleDirectoryCreated(DirectoryCreatedEvent event) {
			s3Service.createDirectory(
					S3Config.Bucket.ROOT,
					FileUtils.buildPath(
							S3Config.Directory.UPLOAD_CLASS,
							Long.toString(event.classId),
							event.directorySlug
					)
			);
		}

	}

	@Transactional
	public DirectoryResponseDto create(
			Long classId,
			Long creatorUserId,
			CreateDirectoryRequestDto request
	) {
		String slug = SlugGenerator.generate(request.getName());

		lockSlug(slug);

		if (existsBySlug(slug)) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Directory already exists"
			);
		}

		Directory newDirectory = directoryMapper.toDirectory(request);

		Class clazz = Class.create(classId);
		User creator = User.create(creatorUserId);

		newDirectory.setClazz(clazz);
		newDirectory.setCreator(creator);

		Directory responseDirectory = save(newDirectory);

		applicationEventPublisher.publishEvent(
				new DirectoryCreatedEvent(
						responseDirectory.getClazz().getId(),
						responseDirectory.getSlug()
				)
		);

		return directoryMapper.toDirectoryResponseDto(responseDirectory);
	}

	@Transactional
	public void lockSlug(String slug) {
		directoryRepository.lockSlug(slug);
	}

	@Transactional(readOnly = true)
	public List<DirectoryResponseDto> getList(Long classId) {
		return getByClassId(classId);
	}

	@Transactional(readOnly = true)
	public boolean existsById(Long id) {
		return directoryRepository.existsById(id);
	}

	@Transactional(readOnly = true)
	public Directory findByIdOrThrow(Long id) {
		return directoryRepository
				.findById(id)
				.orElseThrow(() ->
						new GlobalException(
								GlobalException.Type.NOT_EXISTS,
								"Directory not exists"
						)
				);
	}

	@Transactional(readOnly = true)
	public void throwIfNotExistsById(Long id) {
		if (!existsById(id)) {
			throw new GlobalException(
					GlobalException.Type.NOT_EXISTS,
					"Directory not exists"
			);
		}
	}

	@Transactional(readOnly = true)
	public List<DirectoryResponseDto> getByClassId(Long classId) {
		return directoryRepository.getByClazz_Id(classId);
	}

	@Transactional(readOnly = true)
	public boolean existsBySlug(String slug) {
		return directoryRepository.existsBySlug(slug);
	}

	@Transactional
	public Directory save(Directory directory) {
		return directoryRepository.save(directory);
	}

}