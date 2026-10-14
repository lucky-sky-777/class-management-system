package com.mezon.classmanagement.backend.domain_document.component.chunk.strategy;

import com.mezon.classmanagement.backend.common.misc.Nameable;
import com.mezon.classmanagement.backend.domain_document.component.document_source.DocumentSource;

import java.util.List;

public interface ChunkStrategy extends Nameable {

	List<String> getChunkList(DocumentSource documentSource) throws Exception;

}