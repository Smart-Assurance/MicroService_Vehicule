package ma.fstt.microservicevehicule.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fstt.microservicevehicule.entities.Client;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class AddNewContractRequest {
    @NotBlank
    private String type;
    @NotBlank
    private String matricule;
    @NotBlank
    private double price;
    @NotBlank
    private String grey_card_number;
    @NotBlank
    private String model;
    @NotBlank
    private String contractId;
}