package com.mezon.classmanagement.backend.domain_document.main.search.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDtoProjection;


@JsonInclude(JsonInclude.Include.NON_NULL)
public interface SearchResultDtoProjection extends DocumentResponseDtoProjection {



    @JsonProperty("class_id")
    Long getClassId();

    @JsonProperty("class_name")
    String getClassName();

    @JsonProperty("directory_id")
    Long getDirectoryId();

    @JsonProperty("directory_name")
    String getDirectoryName();

    @JsonProperty("chunk_index")
    Short getChunkIndex();

    @JsonProperty("chunk_text")
    String getChunkText();

    @JsonProperty("score")
    Double getScore();

    @JsonProperty("match_type")
    String getMatchType();

}
