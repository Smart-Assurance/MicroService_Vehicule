package ma.fstt.microservicevehicule.controllers;

import ma.fstt.microservicevehicule.entities.Client;
import ma.fstt.microservicevehicule.entities.Contract;
import ma.fstt.microservicevehicule.entities.Vehicule;
import ma.fstt.microservicevehicule.payload.request.AddNewContractRequest;
import ma.fstt.microservicevehicule.payload.response.MessageResponse;
import ma.fstt.microservicevehicule.payload.response.VehiculeWithClientAndContract;
import ma.fstt.microservicevehicule.repository.ClientRepository;
import ma.fstt.microservicevehicule.repository.ContractRepository;
import ma.fstt.microservicevehicule.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    @Autowired
    public ContractRepository contractRepository;
    @Autowired
    public VehiculeRepository vehiculeRepository;
    @Autowired
    public ClientRepository clientRepository;


    @PostMapping("/contract")
    public ResponseEntity<MessageResponse> addContractForVehicule(@RequestBody AddNewContractRequest addNewContractRequest) {
        try {

            Contract targetContract;
            Client targetClient;


            //test if vehicule not exist with matricule
            Optional<Vehicule> canExistVehicule = vehiculeRepository.findByMatricule(addNewContractRequest.getMatricule());
            if (canExistVehicule.isPresent())
                return ResponseEntity.badRequest().body(new MessageResponse(404, "Vehicule has already a contract"));

            //search for owner by id
            Optional<Client> owner = clientRepository.findById(addNewContractRequest.getClientId());

            if (!owner.isPresent())
                return ResponseEntity.badRequest().body(new MessageResponse(401, "Client not found"));
            targetClient=owner.get();
            //search for contract by id
            Optional<Contract> contract = contractRepository.findById(addNewContractRequest.getContractId());

            if (!contract.isPresent())
                return ResponseEntity.badRequest().body(new MessageResponse(401, "Contract not found"));
            targetContract=contract.get();
            Vehicule vehicule = new Vehicule(addNewContractRequest.getType(), addNewContractRequest.getMatricule(), addNewContractRequest.getPrice(), addNewContractRequest.getGrey_card_number(), addNewContractRequest.getModel(), owner.get(), contract.get());

            vehiculeRepository.save(vehicule);
             Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        // add vehicule to client
            List <Vehicule> existantVehicules = targetClient.getVehicles();
            if(existantVehicules==null)
                existantVehicules = new ArrayList<>();
            existantVehicules.add(savedVehicule);

            targetClient.setVehicles(existantVehicules);

            clientRepository.save(targetClient);


            //add vehicule to contract type
            existantVehicules = targetContract.getAssured_vehicules();
            if(existantVehicules==null)
                existantVehicules = new ArrayList<>();
            existantVehicules.add(savedVehicule);
            targetContract.setAssured_vehicules(existantVehicules);

            contractRepository.save(targetContract);



            return ResponseEntity.ok(new MessageResponse(201, "Vehicule saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400, "Vehicule doesn't save "));
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Vehicule>> getAllVehicules() {
        try {
            List<Vehicule> vehicules = vehiculeRepository.findAll();

            return ResponseEntity.ok(vehicules);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }

    @GetMapping("/{vehiculeId}")
    public ResponseEntity<VehiculeWithClientAndContract> getVehiculeWithClientById(@PathVariable String vehiculeId) {
        try {
            VehiculeWithClientAndContract vehiculeWithClient = vehiculeRepository.findVehiculeWithClientAndContract(vehiculeId);

            if (vehiculeWithClient != null) {
                return ResponseEntity.ok(vehiculeWithClient);
            } else {
                return ResponseEntity.status(404).build(); // Resource not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }
    }

}