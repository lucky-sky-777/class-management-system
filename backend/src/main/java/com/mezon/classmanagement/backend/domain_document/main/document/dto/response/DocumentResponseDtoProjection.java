package com.mezon.classmanagement.backend.domain_document.main.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mezon.classmanagement.backend.common.constant.DateTimeConstant;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DocumentResponseDtoProjection {

	@JsonProperty("id")
	Long getId();

	@JsonProperty("title")
	String getTitle();

	@JsonProperty("description")
	String getDescription();

	@JsonProperty("file_name")
	String getFileName();

	@JsonProperty("view_count")
	Long getViewCount();

	@JsonProperty("download_count")
	Long getDownloadCount();

	@JsonProperty("bookmark_count")
	Long getBookmarkCount();

	@JsonProperty("is_bookmarked")
	Boolean getIsBookmarked();

	@JsonProperty("creator_user_id")
	Long getCreatorUserId();

	@JsonProperty("creator_display_name")
	String getCreatorDisplayName();

	@JsonProperty("creator_avatar_url")
	String getCreatorAvatarUrl();

	@JsonFormat(
			pattern = DateTimeConstant.PATTERN_FULL_DATETIME,
			timezone = DateTimeConstant.TIMEZONE
	)
	@JsonProperty("created_at")
	Instant getCreatedAt();

	@JsonProperty("status")
	Document.Status getStatus();

}