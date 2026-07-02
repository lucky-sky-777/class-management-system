package com.mezon.classmanagement.backend.domain_ai.dto.batch;



import com.mezon.classmanagement.backend.domain_ai.dto.text.EmbedIndividualRequest;

import java.util.List;

public record GeminiBatchRequest(List<EmbedIndividualRequest> requests) {}