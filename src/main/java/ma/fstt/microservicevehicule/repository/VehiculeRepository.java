package ma.fstt.microservicevehicule.repository;

import ma.fstt.microservicevehicule.entities.Client;
import ma.fstt.microservicevehicule.entities.Vehicule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends MongoRepository<Vehicule, String> {
    Optional<Vehicule> findByMatricule(String matricule);
    @Query(value = "{ '_id': ?0 }")
    Vehicule findVehiculeWithClientAndContract(String vehiculeId);

    List<Vehicule> findAllByOwner(Client owner);

}
