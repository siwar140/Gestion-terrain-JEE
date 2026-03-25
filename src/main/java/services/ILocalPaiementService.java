package services;

import entities.Paiement;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ILocalPaiementService {
    void addPaiement(Paiement paiement);
    void updatePaiement(Paiement paiement);
    void deletePaiement(Long id);
    Paiement getPaiementById(Long id);
    List<Paiement> getAllPaiements();
    
    // Méthodes supplémentaires
    List<Paiement> getPaiementsParClient(Long idClient);
    List<Paiement> getPaiementsParReservation(Long idReservation);
    
    // ========== NOUVELLES MÉTHODES AJOUTÉES POUR LE SERVLET ==========
    
    /**
     * Méthode de débogage pour vérifier l'état d'un paiement
     */
    void debugPaiement(Long id);
    
    /**
     * Vérifie si un paiement peut être supprimé
     */
    boolean canDeletePaiement(Long id);
    
    /**
     * Force la suppression d'un paiement (méthode radicale)
     */
    boolean forceDeletePaiement(Long id);
    
    /**
     * Obtenir le total des paiements d'un client
     */
    Double getTotalPaiementsClient(Long idClient);
    
    /**
     * Obtenir le total des paiements d'une réservation
     */
    Double getTotalPaiementsReservation(Long idReservation);
    
    /**
     * Vérifie si un paiement existe
     */
    boolean paiementExists(Long id);
    
    /**
     * Supprime tous les paiements d'un client
     */
    int deleteAllPaiementsClient(Long idClient);
    
    /**
     * Trouve les paiements par mode de paiement
     */
    List<Paiement> getPaiementsParMode(String modePaiement);
    
    /**
     * Compte le nombre total de paiements
     */
    long countPaiements();
}