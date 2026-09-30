package com.mezon.classmanagement.backend.domain_document.main.friend.follower.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.auth.mapper.UserMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.dto.FollowerResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.follower.entity.Follower;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapStructConfig.class,
        uses = {UserMapper.class}
)
public interface FollowerMapper {

    @Mapping(source = "follower", target = "requester")
    FollowerResponseDto toFollowerResponseDto(Follower follower);

}
