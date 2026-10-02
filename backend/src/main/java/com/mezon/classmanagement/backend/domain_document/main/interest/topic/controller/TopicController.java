package com.mezon.classmanagement.backend.domain_document.main.interest.topic.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request.CreateTopicRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request.UpdateTopicRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response.TopicIdResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response.TopicResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.service.TopicService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings({WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @Valid @RequestBody CreateTopicRequest request
    ) {
        TopicResponse response = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateTopicRequest request
    ) {
        TopicResponse response = topicService.updateTopic(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TopicIdResponse> deleteTopic(
            @PathVariable("id") Long id
    ) {
        TopicIdResponse response = topicService.deleteTopic(id);
        return ResponseEntity.ok(response);
    }
}