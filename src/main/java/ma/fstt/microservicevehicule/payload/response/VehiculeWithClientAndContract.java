package ma.fstt.microservicevehicule.payload.response;

import ma.fstt.microservicevehicule.entities.Client;
import ma.fstt.microservicevehicule.entities.Contract;

public interface VehiculeWithClientAndContract {
    String getId();
    String getType();
    String getMatricule();
    double getPrice();
    String getGrey_card_number();
    String getModel();
    Client getOwner();
    Contract getContract();
}
