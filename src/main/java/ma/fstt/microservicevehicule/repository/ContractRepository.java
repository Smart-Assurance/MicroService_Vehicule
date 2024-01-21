package ma.fstt.microservicevehicule.repository;

import ma.fstt.microservicevehicule.entities.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {

}