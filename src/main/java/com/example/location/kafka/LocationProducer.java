package com.example.location.kafka;
// import com.example.location.dto.LocationUpdateRequest;
// import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class LocationProducer {

    // private static final String TOPIC = "location-updates";

    // private final KafkaTemplate<String, LocationUpdateRequest> kafkaTemplate;

    // public LocationProducer(KafkaTemplate<String, LocationUpdateRequest> kafkaTemplate) {
    //     this.kafkaTemplate = kafkaTemplate;
    // }

    // public void send(LocationUpdateRequest request) {
    //     // Use userId as partition key (converted to String for serializer compatibility)
    //     String key = request.getUserId().toString();
    //     kafkaTemplate.send(TOPIC, key, request);

    // }
}
