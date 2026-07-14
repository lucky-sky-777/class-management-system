package com.mezon.classmanagement.backend_document.domain.rabbitmq;

import com.mezon.classmanagement.backend_document.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FileConsumer {

	@RabbitListener(queues = RabbitConfig.FILE_QUEUE)
	public void receive(String message) {

		System.out.println(message);

	}

}