package com.mezon.classmanagement.backend.domain_document.main.friend.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder(value = {
        "friend_count",
        "follower_count",
        "following_count"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class FriendSummaryResponseDto {

    @JsonProperty(value = "friend_count")
    Long friendCount;

    @JsonProperty(value = "follower_count")
    Long followerCount;

    @JsonProperty(value = "following_count")
    Long followingCount;

}
