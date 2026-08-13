package com.mezon.classmanagement.backend_document.domain.rabbitmq;

import com.mezon.classmanagement.backend_document.config.RabbitConfig;
import com.mezon.classmanagement.backend_document.domain.component.ingest.dto.RabbitIngestRequest;
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

	public void send(String filePath) {
		RabbitIngestRequest message = new RabbitIngestRequest(filePath);

		rabbitTemplate.convertAndSend(
				RabbitConfig.FILE_QUEUE,
				message
		);
	}

}