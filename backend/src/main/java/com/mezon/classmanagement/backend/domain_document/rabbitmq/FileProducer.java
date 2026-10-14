package com.mezon.classmanagement.backend.domain_document.rabbitmq;

import com.mezon.classmanagement.backend.config.RabbitConfig;
import com.mezon.classmanagement.backend.domain_document.component.ingest.dto.RabbitIngestRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class FileProducer {

	RabbitTemplate rabbitTemplate;

	public void send(String tempFilePath, String originalFileName) {
		RabbitIngestRequest message = new RabbitIngestRequest(tempFilePath, originalFileName);

		rabbitTemplate.convertAndSend(
				RabbitConfig.FILE_QUEUE,
				message
		);
	}

}