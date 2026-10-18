package com.mezon.classmanagement.backend.domain_document.main.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@JsonPropertyOrder(value = {
		"id",
		"title",
		"description",
		"file_name",
		"creator_user_id",
		"created_at",
		"status"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DTO
public class DocumentResponseDto {

	@JsonProperty(value = "id")
	Long id;

	@SuppressWarnings({"all"})
	// bỏ qua cái này nha
	// để tạm dùng nội bộ thôi
	// do chưa có cách xử lý
	String key;

	@JsonProperty(value = "title")
	String title;

	@JsonProperty(value = "description")
	String description;

	@JsonProperty(value = "file_name")
	String fileName;

	@JsonProperty(value = "creator_user_id")
	String creatorUserId;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "created_at")
	Instant createdAt;

	@JsonProperty(value = "status")
	String status;

}