package com.mezon.classmanagement.backend.domain_document.main.interest.topic.mapper;

import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.request.CreateTopicRequest;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.dto.response.TopicResponse;
import com.mezon.classmanagement.backend.domain_document.main.interest.topic.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    // Chuyển từ Request DTO sang Entity (Lúc này slug chưa có, sẽ set ở Service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    Topic toTopic(CreateTopicRequest request);

    // Chuyển từ Entity sang Response DTO
    TopicResponse toTopicResponse(Topic topic);
}