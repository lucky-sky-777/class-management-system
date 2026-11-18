package com.mezon.classmanagement.backend.domain_document.main.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.annotation.DTO;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
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
		"view_count",
		"download_count",
		"bookmark_count",
		"is_bookmarked",
		"creator_user_id",
		"creator_display_name",
		"creator_avatar_url",
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

	@JsonProperty(value = "title")
	String title;

	@JsonProperty(value = "description")
	String description;

	@JsonProperty(value = "file_name")
	String fileName;

	@JsonProperty(value = "view_count")
	Long viewCount;

	@JsonProperty(value = "download_count")
	Long downloadCount;

	@JsonProperty(value = "bookmark_count")
	Long bookmarkCount;

	@JsonProperty(value = "is_bookmarked")
	Boolean isBookmarked;

	@JsonProperty(value = "creator_user_id")
	Long creatorUserId;

	@JsonProperty(value = "creator_display_name")
	String creatorDisplayName;

	@JsonProperty(value = "creator_avatar_url")
	String creatorAvatarUrl;

	@JsonFormat(pattern = DateTimeConstant.PATTERN_FULL_DATETIME, timezone = DateTimeConstant.TIMEZONE)
	@JsonProperty(value = "created_at")
	Instant createdAt;

	@JsonProperty(value = "status")
	Document.Status status;

}