package ma.fstt.microservicevehicule.repository;

import ma.fstt.microservicevehicule.entities.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

}