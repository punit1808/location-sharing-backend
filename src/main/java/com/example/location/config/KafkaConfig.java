// config/KafkaConfig.java
package com.example.location.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic locationTopic(@Value("${kafka.topics.location}") String name) {
        // 6 partitions example; adjust as needed
        return new NewTopic(name, 6, (short)1);
    }
}