package services;

import entities.Reservation;
import jakarta.ejb.Local;
import java.util.Date;
import java.util.List;

@Local
public interface ILocalReservationService {
    // CRUD
    void addReservation(Reservation reservation);
    void updateReservation(Reservation reservation);
    void deleteReservation(Long id);
    Reservation getReservationById(Long id);
    List<Reservation> getAllReservations();
    void updateStatutPaiement(Long idReservation, boolean estPaye, String modePaiement);

    // Requêtes spécifiques
    List<Reservation> getReservationsMars2025();
    void deleteReservationsAvantDate(Date date);
    void updateModePaiementReservation(Long idReservation, String modePaiement);
    public void synchroniserToutesReservationsAvecPaiements();

    // Méthodes utilitaires
    List<Reservation> getReservationsClient(Long idClient, Date debut, Date fin);
    List<Reservation> getReservationsParDate(Date date);
    void updateModePaiementReservationById(Long idReservation, String modePaiement);
    
    // Pour la méthode spécifique avec ID 202
    void updateModePaiementReservation202(String modePaiement);

    // Méthodes ajoutées pour les réservations payées
    List<Reservation> getReservationsPayees();
    List<Reservation> getReservationsNonPayees();
}