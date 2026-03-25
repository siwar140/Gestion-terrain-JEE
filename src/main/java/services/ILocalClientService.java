package services;

import entities.Client;
import java.util.List;

public interface ILocalClientService {
    
    // Modification: retourne boolean pour indiquer le succès/échec
    boolean deleteClient(Long id);
    
    // Version alternative avec gestion des contraintes
    boolean deleteClientWithConstraints(Long clientId);
    
    // Autres méthodes
    void addClient(Client client);
    void updateClient(Client client);
    Client getClientById(Long id);
    List<Client> getAllClients();
    List<Object[]> getClientsFootballTries();
    List<Object[]> getMontantTotalParClient();
    List<Client> rechercherClientsParNom(String nom);
    List<Object[]> getHistoriqueReservationsClient(Long idClient);
}