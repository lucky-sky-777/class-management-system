package com.mezon.classmanagement.backend.domain.groupuser.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public final class UpdateGroupUserSeatRequestDto {

	@JsonProperty(value = "source_group_id")
	Long sourceGroupId;

	@JsonProperty(value = "source_desk")
	Short sourceDesk;

	@JsonProperty(value = "source_desk_position")
	Short sourceDeskPosition;

	@JsonProperty(value = "target_group_id")
	Long targetGroupId;

	@JsonProperty(value = "target_desk")
	Short targetDesk;

	@JsonProperty(value = "target_desk_position")
	Short targetDeskPosition;

}