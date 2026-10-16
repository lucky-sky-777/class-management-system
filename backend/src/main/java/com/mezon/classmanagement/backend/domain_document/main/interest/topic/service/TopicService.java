package com.mezon.classmanagement.backend.domain_document.main.interest.topic.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request.CreateTopicRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request.UpdateTopicRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response.TopicIdResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response.TopicResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.entity.Topic;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.repository.TopicRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TopicService {

    TopicRepository topicRepository;

    @Transactional
    public TopicResponse createTopic(CreateTopicRequest request) {
        // Lấy trực tiếp slug từ request
        String slug = request.getSlug();

        // Kiểm tra xem slug đã tồn tại chưa
        throwIfExistsBySlug(slug);

        Topic newTopic = Topic.builder()
                .name(request.getName())
                .slug(slug)
                .build();

        Topic responseTopic = save(newTopic);

        return TopicResponse.builder()
                .id(responseTopic.getId())
                .name(responseTopic.getName())
                .slug(responseTopic.getSlug())
                .build();
    }

    @Transactional
    public TopicResponse updateTopic(Long topicId, UpdateTopicRequest request){
        Topic currentTopic = findByIdOrThrow(topicId);

        // Nếu client cập nhật slug mới khác với slug hiện tại -> Kiểm tra xem slug mới đã bị trùng chưa
        if (!currentTopic.getSlug().equals(request.getSlug())) {
            throwIfExistsBySlug(request.getSlug());
        }

        currentTopic.setName(request.getName());
        currentTopic.setSlug(request.getSlug());

        Topic responseTopic = save(currentTopic);

        return TopicResponse.builder()
                .id(responseTopic.getId())
                .name(responseTopic.getName())
                .slug(responseTopic.getSlug())
                .build();
    }

    @Transactional
    public TopicIdResponse deleteTopic(Long topicId) {
        Topic currentTopic = findByIdOrThrow(topicId);

        delete(currentTopic);

        return TopicIdResponse.builder()
                .topicId(currentTopic.getId())
                .build();
    }

    @Transactional
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }

    @Transactional
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return topicRepository.existsBySlug(slug);
    }

    @Transactional(readOnly = true)
    public void throwIfExistsBySlug(String slug) {
        if (existsBySlug(slug)) {
            throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Topic already exists");
        }
    }

    @Transactional(readOnly = true)
    public Topic findByIdOrThrow(Long id) {
        return topicRepository
                .findById(id)
                .orElseThrow(() ->
                        new GlobalException(GlobalException.Type.NOT_FOUND, "Topic not found")
                );
    }
}