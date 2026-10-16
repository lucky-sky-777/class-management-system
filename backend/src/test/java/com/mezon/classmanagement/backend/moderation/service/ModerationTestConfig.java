package com.mezon.classmanagement.backend.moderation.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


// Cấu hình các component cần thiết để chạy Integration Test cho moderation.

@Configuration
@ComponentScan(basePackages = {
        "com.mezon.classmanagement.backend.domain_document.component.chunk",
        "com.mezon.classmanagement.backend.domain_document.component.split",
        "com.mezon.classmanagement.backend.domain_document.main.moderation"
})
public class ModerationTestConfig {
}