package ma.fstt.microservicevehicule.controllers;

import ma.fstt.microservicevehicule.entities.Client;
import ma.fstt.microservicevehicule.entities.Contract;
import ma.fstt.microservicevehicule.entities.Vehicule;
import ma.fstt.microservicevehicule.payload.request.AddNewContractRequest;
import ma.fstt.microservicevehicule.payload.response.MessageResponse;
import ma.fstt.microservicevehicule.repository.ClientRepository;
import ma.fstt.microservicevehicule.repository.ContractRepository;
import ma.fstt.microservicevehicule.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final AuthService authService;

    public VehiculeController(ContractRepository contractRepository, VehiculeRepository vehiculeRepository, ClientRepository clientRepository,AuthService authService) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.authService = authService;
    }

    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }


    @PostMapping("/contract")
    public ResponseEntity<MessageResponse> addContractForVehicule(@RequestBody AddNewContractRequest addNewContractRequest,@RequestHeader("Authorization") String authorizationHeader) {
        try {

            Contract targetContract;
            Client targetClient;

            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidClientToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }


            //test if vehicule not exist with matricule
            Optional<Vehicule> canExistVehicule = vehiculeRepository.findByMatricule(addNewContractRequest.getMatricule());
            if (canExistVehicule.isPresent())
                return ResponseEntity.badRequest().body(new MessageResponse(404, "Vehicule has already a contract"));


            //search for owner by id
            String id = authService.getIdFromToken(token);
            Optional<Client> owner = clientRepository.findById(id);

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
    public ResponseEntity<Object> getAllVehicules(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token) && !authService.isValidExaminaterToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }

            List<Vehicule> vehicules = vehiculeRepository.findAll();

            return ResponseEntity.ok(vehicules);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }

    @GetMapping("/{vehiculeId}")
    public ResponseEntity<Object> getVehiculeWithClientById(@PathVariable String vehiculeId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token) && !authService.isValidExaminaterToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }

            Vehicule vehiculeWithClient = vehiculeRepository.findVehiculeWithClientAndContract(vehiculeId);

            if (vehiculeWithClient != null) {
                return ResponseEntity.ok(vehiculeWithClient);
            } else {
                return ResponseEntity.status(404).build(); // Resource not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }
    }

    @GetMapping("/getMyContracts")
    public ResponseEntity<Object> getMyVehicules(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidClientToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }

            String id = authService.getIdFromToken(token);

            Optional<Client> clientOptional = clientRepository.findById(id);


            List<Vehicule> vehiculeWithContract = vehiculeRepository.findAllByOwner(clientOptional.get());

            if (vehiculeWithContract != null) {
                return ResponseEntity.ok(vehiculeWithContract);
            } else {
                return ResponseEntity.status(404).build(); // Resource not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }

}