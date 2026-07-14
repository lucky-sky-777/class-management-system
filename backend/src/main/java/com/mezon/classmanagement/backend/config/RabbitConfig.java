package com.mezon.classmanagement.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String FILE_QUEUE = "file.queue";

	@Bean
	Queue queue() {
		return new Queue(FILE_QUEUE, true);
	}

}