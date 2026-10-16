package com.mezon.classmanagement.backend.domain_document.main.interest.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.request.CreateInterestRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.request.UpdateInterestRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.dto.response.InterestResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.interest.entity.Interest;
import com.mezon.classmanagement.backend.domain_document.main.interest.repository.InterestRepository;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.entity.Topic;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.repository.TopicRepository;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.service.TopicService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class InterestService {
    InterestRepository interestRepository;
    TopicRepository topicRepository;

    @Transactional
    public InterestResponse registerOrUpdateInterests(Long clientUserId, UpdateInterestRequest request) {
        List<Long> requestedTopicIds = request.getTopicIds();

        // 1. (Optional) Validate: Đảm bảo các topicId gửi lên đều tồn tại trong DB
        if (requestedTopicIds != null && !requestedTopicIds.isEmpty()) {
            long existingTopicsCount = topicRepository.countByIdIn(requestedTopicIds);
            if (existingTopicsCount != requestedTopicIds.size()) {
                throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Một hoặc nhiều Topic ID không tồn tại");
            }
        }

        // 2. Lấy bản ghi Interest hiện tại của User (nếu chưa có thì tạo mới)
        Interest currentInterest = interestRepository.findByUserId(clientUserId)
                .orElseGet(() -> Interest.builder()
                        .user(User.builder().id(clientUserId).build())
                        .build());

        // 3. Ghi đè danh sách topicIds mới
        currentInterest.setTopicIds(requestedTopicIds);

        // 4. Lưu vào Database
        Interest savedInterest = save(currentInterest);

        // 5. Build Response
        return InterestResponse.builder()
                .id(savedInterest.getId())
                .userId(savedInterest.getUser().getId())
                .topicIds(savedInterest.getTopicIds())
                .build();
    }

    /**
     * - Action
     */
    @Transactional
    public Interest save(Interest interest) {
        return interestRepository.save(interest);
    }
}
