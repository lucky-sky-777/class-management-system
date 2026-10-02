package com.mezon.classmanagement.backend.domain_document.main.directory.service;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.directory.mapper.DirectoryMapper;
import com.mezon.classmanagement.backend.domain_document.main.directory.repository.DirectoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DirectoryService {

	DirectoryRepository directoryRepository;

	DirectoryMapper directoryMapper;

	S3Service s3Service;

	ApplicationEventPublisher applicationEventPublisher;

	public record DirectoryCreatedEvent(Long classId, Long userId) {}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	@Component
	public class DirectoryEventListener {

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
		public void handleDirectoryCreated(DirectoryCreatedEvent event) {

		}
	}

	@Transactional
	public void createForClass(Long classId, Long creatorUserId, CreateDirectoryRequestDto request) {
		Directory newDirectory = directoryMapper.toDirectory(request);

		User creator = User.create(creatorUserId);
		Class clazz = Class.create(classId);

		newDirectory.setClazz(clazz);
		newDirectory.setCreator(creator);

		Directory responseDirectory = save(newDirectory);
	}

	@Transactional
	public Directory save(Directory directory) {
		return directoryRepository.save(directory);
	}

}