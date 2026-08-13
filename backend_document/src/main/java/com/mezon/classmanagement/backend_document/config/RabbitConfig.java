package com.mezon.classmanagement.backend_document.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String FILE_QUEUE = "file.queue";

	@Bean
	public Queue queue() {
		return new Queue(FILE_QUEUE, true);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new JacksonJsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
			ConnectionFactory connectionFactory,
			MessageConverter messageConverter
	) {
		RabbitTemplate rabbitTemplate =
				new RabbitTemplate(connectionFactory);

		rabbitTemplate.setMessageConverter(messageConverter);

		return rabbitTemplate;
	}

}