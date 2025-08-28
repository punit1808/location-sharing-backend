package com.example.location.config;

import com.example.location.dto.LocationUpdateRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, LocationUpdateRequest> producerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.properties.security.protocol}") String securityProtocol,
            @Value("${spring.kafka.properties.sasl.mechanism}") String saslMechanism,
            @Value("${spring.kafka.properties.sasl.jaas.config}") String jaasConfig,
            @Value("${spring.kafka.properties.ssl.truststore.location}") String truststoreLocation,
            @Value("${spring.kafka.properties.ssl.truststore.password}") String truststorePassword,
            @Value("${spring.kafka.properties.ssl.truststore.type}") String truststoreType
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Security configs
        props.put("security.protocol", securityProtocol);
        props.put("sasl.mechanism", saslMechanism);
        props.put("sasl.jaas.config", jaasConfig);
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.truststore.password", truststorePassword);
        props.put("ssl.truststore.type", truststoreType);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LocationUpdateRequest> kafkaTemplate(
            ProducerFactory<String, LocationUpdateRequest> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
