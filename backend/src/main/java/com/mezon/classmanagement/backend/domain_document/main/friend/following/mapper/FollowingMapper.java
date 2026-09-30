package com.mezon.classmanagement.backend.domain_document.main.friend.following.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.auth.mapper.UserMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.dto.FollowingResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.following.entity.Following;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapStructConfig.class,
        uses = {UserMapper.class}
)
public interface FollowingMapper {

    @Mapping(source = "following", target = "targetUser")
    FollowingResponseDto toFollowingResponseDto(Following following);

}
