package com.mezon.classmanagement.backend.domain_document.main.friend.friend.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.auth.mapper.UserMapper;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto.FriendResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.friend.friend.entity.Friend;
import org.mapstruct.Mapper;

@Mapper(
        config = MapStructConfig.class,
        uses = {UserMapper.class}
)
public interface FriendMapper {

    default FriendResponseDto toFriendResponseDto(Friend friend, Long clientUserId) {
        if (friend == null) {
            return null;
        }
        User friendUser = (friend.getUser1() != null && friend.getUser1().getId().equals(clientUserId))
                ? friend.getUser2()
                : friend.getUser1();

        return FriendResponseDto.builder()
                .id(friend.getId())
                .friend(toUserResponseDto(friendUser))
                .friendedAt(friend.getFriendedAt())
                .build();
    }

    UserResponseDto toUserResponseDto(User user);

}
