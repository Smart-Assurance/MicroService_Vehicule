package ma.fstt.microservicevehicule.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    String kafkaTopic = "Features";

    public void send(String key,String message) {
        kafkaTemplate.send(kafkaTopic, key, message);
    }
}