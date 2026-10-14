package com.mezon.classmanagement.backend.domain_document.main.document.dto.response;

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

	@JsonProperty(value = "title")
	String title;

	@JsonProperty(value = "description")
	String description;

	@JsonProperty(value = "file_name")
	String fileName;

	@JsonProperty(value = "creator_user_id")
	String creatorUserId;

}