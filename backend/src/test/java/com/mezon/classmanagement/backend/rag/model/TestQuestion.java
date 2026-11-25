package com.mezon.classmanagement.backend.rag.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record TestQuestion(

        @JsonProperty("query_id")
        String queryId,

        @JsonProperty("paragraph_id")
        int paragraphId,

        String question,

        JsonNode answers,

        String context

) {}