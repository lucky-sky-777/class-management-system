package com.mezon.classmanagement.backend;

import com.mezon.classmanagement.backend.config.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void test() {
		rabbitTemplate.convertAndSend(
				RabbitConfig.FILE_QUEUE,
				"hello"
		);
	}

}