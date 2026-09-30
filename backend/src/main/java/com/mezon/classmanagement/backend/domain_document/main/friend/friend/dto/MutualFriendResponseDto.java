package com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.domain.auth.dto.user.UserResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@JsonPropertyOrder(value = {
        "mutual_count",
        "mutual_friends"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class MutualFriendResponseDto {

    @JsonProperty(value = "mutual_count")
    Long mutualCount;

    @JsonProperty(value = "mutual_friends")
    List<UserResponseDto> mutualFriends;

}
