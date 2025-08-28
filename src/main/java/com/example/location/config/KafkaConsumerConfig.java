package com.example.location.config;

import com.example.location.dto.LocationUpdateRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, LocationUpdateRequest> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.properties.security.protocol}") String securityProtocol,
            @Value("${spring.kafka.properties.sasl.mechanism}") String saslMechanism,
            @Value("${spring.kafka.properties.sasl.jaas.config}") String jaasConfig,
            @Value("${spring.kafka.properties.ssl.truststore.location}") String truststoreLocation,
            @Value("${spring.kafka.properties.ssl.truststore.password}") String truststorePassword,
            @Value("${spring.kafka.properties.ssl.truststore.type}") String truststoreType
    ) {
        JsonDeserializer<LocationUpdateRequest> deserializer = new JsonDeserializer<>(LocationUpdateRequest.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "location-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        // Security configs
        props.put("security.protocol", securityProtocol);
        props.put("sasl.mechanism", saslMechanism);
        props.put("sasl.jaas.config", jaasConfig);
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.truststore.password", truststorePassword);
        props.put("ssl.truststore.type", truststoreType);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LocationUpdateRequest> kafkaListenerContainerFactory(
            ConsumerFactory<String, LocationUpdateRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, LocationUpdateRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
