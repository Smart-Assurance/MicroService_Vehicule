package ma.fstt.microservicevehicule.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.fstt.microservicevehicule.entities.Client;
import ma.fstt.microservicevehicule.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumer {

    @Autowired
    public ClientRepository clientRepository;
    @KafkaListener(topics = "recommandation",groupId = "my-consumer-group")
    public void listen(String message) {
        try {
            // Parse the JSON message
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            // Fetch specific fields from the JSON message
            String idContract = jsonNode.get("id_contract").asText();
            String idClient = jsonNode.get("id_client").asText();

           Optional<Client> clientOptional = clientRepository.findById(idClient);

           if(clientOptional.isPresent()){
               Client client = clientOptional.get();
               client.setRecommandedContract(idContract);
               clientRepository.save(client);
           }

            // Now you can use idContract and idClient as needed in your application logic
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}