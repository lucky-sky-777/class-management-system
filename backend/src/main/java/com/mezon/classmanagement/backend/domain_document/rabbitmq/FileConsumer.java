package com.mezon.classmanagement.backend.domain_document.rabbitmq;

import com.mezon.classmanagement.backend.config.RabbitConfig;
import com.mezon.classmanagement.backend.domain_document.component.ingest.dto.RabbitIngestRequest;
import com.mezon.classmanagement.backend.domain_document.component.ingest.service.IngestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class FileConsumer {

	IngestService ingestService;

	@RabbitListener(queues = RabbitConfig.FILE_QUEUE)
	public void receive(RabbitIngestRequest message) {

		ingestService.ingest(message.filePath());

	}

}