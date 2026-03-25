package services;

import entities.Reservation;
import entities.Client;
import entities.Terrain;
import entities.Paiement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Query;

@Stateless
@Transactional
public class LocalReservationServiceImpl implements ILocalReservationService {
    
    @PersistenceContext(unitName = "gestionTerrainPU")
    private EntityManager em;
    
    @Override
    public void addReservation(Reservation reservation) {
        try {
            System.out.println("🔄 Ajout réservation pour client: " + 
                (reservation.getClient() != null ? reservation.getClient().getNom() : "null"));
            
            // LOGIQUE CORRIGÉE POUR LE STATUT DE PAIEMENT :
            // 1. Par défaut, la réservation est "non payée"
            String modePaiementParDefaut = "non payé";
            
            // 2. Vérifier si un paiement valide est fourni
            boolean paiementValide = false;
            if (reservation.getPaiement() != null) {
                // Un paiement est valide s'il a un montant > 0 ET un mode de paiement défini
                if (reservation.getPaiement().getMontant() > 0 && 
                    reservation.getPaiement().getModePaiement() != null && 
                    !reservation.getPaiement().getModePaiement().trim().isEmpty()) {
                    
                    paiementValide = true;
                    System.out.println("💰 Paiement valide détecté - Montant: " + 
                        reservation.getPaiement().getMontant() + 
                        ", Mode: " + reservation.getPaiement().getModePaiement());
                } else {
                    System.out.println("⚠️ Paiement invalide ou incomplet - Montant: " + 
                        reservation.getPaiement().getMontant() + 
                        ", Mode: " + reservation.getPaiement().getModePaiement());
                }
            }
            
            // 3. Déterminer le mode de paiement final
            if (paiementValide) {
                // Si paiement valide, utiliser le mode du paiement
                reservation.setModePaiement(reservation.getPaiement().getModePaiement());
                System.out.println("💰 Statut défini via paiement: " + reservation.getModePaiement());
            } else {
                // Sinon, forcer "non payé"
                reservation.setModePaiement(modePaiementParDefaut);
                System.out.println("💰 Statut défini: " + modePaiementParDefaut + " (pas de paiement valide)");
                
                // Important: si un paiement invalide est attaché, le dissocier
                if (reservation.getPaiement() != null) {
                    System.out.println("⚠️ Dissociation du paiement invalide");
                    reservation.setPaiement(null);
                }
            }
            
            // S'assurer que les relations sont correctement établies
            if (reservation.getClient() != null && reservation.getClient().getIdClient() != null) {
                reservation.setClient(em.merge(reservation.getClient()));
            }
            
            if (reservation.getTerrain() != null && reservation.getTerrain().getIdTerrain() != null) {
                reservation.setTerrain(em.merge(reservation.getTerrain()));
            }
            
            // S'il y a un paiement VALIDE, l'associer
            if (paiementValide && reservation.getPaiement() != null) {
                reservation.getPaiement().setReservation(reservation);
                if (reservation.getClient() != null) {
                    reservation.getPaiement().setClient(reservation.getClient());
                }
                em.persist(reservation.getPaiement());
            }
            
            em.persist(reservation);
            em.flush();
            
            System.out.println("✅ Réservation ajoutée ID: " + reservation.getIdReservation() + 
                              " - Statut: " + reservation.getModePaiement());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur addReservation: " + e.getMessage());
            throw new RuntimeException("Erreur ajout réservation: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void updateReservation(Reservation reservation) {
        try {
            System.out.println("🔄 Mise à jour réservation ID: " + reservation.getIdReservation());
            
            // S'assurer que l'entité est managée
            if (!em.contains(reservation) && reservation.getIdReservation() != null) {
                Reservation managedReservation = getReservationById(reservation.getIdReservation());
                if (managedReservation != null) {
                    // Copier les valeurs
                    managedReservation.setDateReservation(reservation.getDateReservation());
                    managedReservation.setHeureDebut(reservation.getHeureDebut());
                    managedReservation.setHeureFin(reservation.getHeureFin());
                    managedReservation.setDuree(reservation.getDuree());
                    managedReservation.setModePaiement(reservation.getModePaiement());
                    
                    // Mettre à jour les relations si elles sont fournies et ne sont pas null
                    if (reservation.getClient() != null) {
                        managedReservation.setClient(em.merge(reservation.getClient()));
                    }
                    if (reservation.getTerrain() != null) {
                        managedReservation.setTerrain(em.merge(reservation.getTerrain()));
                    }
                    if (reservation.getPaiement() != null) {
                        managedReservation.setPaiement(em.merge(reservation.getPaiement()));
                    }
                    
                    em.merge(managedReservation);
                    System.out.println("✅ Réservation mise à jour ID: " + managedReservation.getIdReservation());
                    return;
                }
            }
            
            // Si l'entité est déjà managée
            em.merge(reservation);
            System.out.println("✅ Réservation mise à jour");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur updateReservation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur mise à jour réservation: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void updateStatutPaiement(Long idReservation, boolean estPaye, String modePaiement) {
        try {
            Reservation reservation = getReservationById(idReservation);
            
            if (reservation != null) {
                if (estPaye && modePaiement != null && !modePaiement.isEmpty() && !"non payé".equals(modePaiement)) {
                    reservation.setModePaiement(modePaiement);
                    System.out.println("✅ Réservation ID " + idReservation + " marquée comme payée (" + modePaiement + ")");
                } else {
                    reservation.setModePaiement("non payé");
                    System.out.println("✅ Réservation ID " + idReservation + " marquée comme non payée");
                }
                
                em.merge(reservation);
                em.flush();
                
                System.out.println("✅ Statut paiement mis à jour pour réservation ID: " + idReservation + 
                                 " -> " + reservation.getModePaiement());
            } else {
                System.out.println("⚠️ Réservation non trouvée ID: " + idReservation);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur updateStatutPaiement: " + e.getMessage());
            throw new RuntimeException("Erreur mise à jour statut paiement", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteReservation(Long id) {
        try {
            System.out.println("🗑️ SUPPRESSION RÉSERVATION ID: " + id);
            
            // ÉTAPE 1: Charger la réservation avec TOUTES ses dépendances
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.idReservation = :id";
            
            Reservation reservation = em.createQuery(jpql, Reservation.class)
                                      .setParameter("id", id)
                                      .getResultStream()
                                      .findFirst()
                                      .orElse(null);
            
            if (reservation == null) {
                System.out.println("⚠️ Réservation non trouvée ID: " + id);
                return;
            }
            
            System.out.println("✓ Réservation trouvée - Client: " + 
                (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A") +
                " - Terrain: " + (reservation.getTerrain() != null ? reservation.getTerrain().getNom() : "N/A") +
                " - Statut paiement: " + reservation.getModePaiement());
            
            // ÉTAPE 2: Supprimer d'abord le paiement associé (si existe)
            try {
                System.out.println("  - Vérification paiement associé...");
                Paiement paiement = reservation.getPaiement();
                if (paiement != null) {
                    System.out.println("  - Suppression paiement ID: " + paiement.getIdPaiement());
                    
                    // Dissocier le paiement du client
                    if (paiement.getClient() != null) {
                        paiement.getClient().getPaiements().remove(paiement);
                        em.merge(paiement.getClient());
                    }
                    
                    // Supprimer le paiement
                    em.remove(paiement);
                    System.out.println("    ✓ Paiement supprimé");
                    
                    // Dissocier de la réservation
                    reservation.setPaiement(null);
                }
            } catch (Exception e) {
                System.out.println("    ℹ️ Aucun paiement à supprimer: " + e.getMessage());
            }
            
            // ÉTAPE 3: Retirer la réservation du client
            if (reservation.getClient() != null) {
                System.out.println("  - Retrait du client...");
                Client client = reservation.getClient();
                if (client.getReservations() != null) {
                    client.getReservations().remove(reservation);
                    em.merge(client);
                }
                reservation.setClient(null);
                System.out.println("    ✓ Retiré du client");
            }
            
            // ÉTAPE 4: Retirer la réservation du terrain
            if (reservation.getTerrain() != null) {
                System.out.println("  - Retrait du terrain...");
                Terrain terrain = reservation.getTerrain();
                if (terrain.getReservations() != null) {
                    terrain.getReservations().remove(reservation);
                    em.merge(terrain);
                }
                reservation.setTerrain(null);
                System.out.println("    ✓ Retiré du terrain");
            }
            
            // ÉTAPE 5: Supprimer la réservation
            System.out.println("  - Suppression de la réservation...");
            em.remove(reservation);
            
            // ÉTAPE 6: Forcer la suppression
            em.flush();
            
            System.out.println("✅ RÉSERVATION SUPPRIMÉE AVEC SUCCÈS ID: " + id);
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR deleteReservation ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            
            // MÉTHODE ALTERNATIVE: Suppression directe SQL avec noms de tables corrects
            try {
                System.out.println("🔄 TENTATIVE AVEC SUPPRESSION SQL DIRECTE...");
                
                // Désactiver temporairement les contraintes (MySQL)
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
                
                // 1. Supprimer d'abord les paiements liés à cette réservation
                int paiementsSupprimes = em.createNativeQuery(
                    "DELETE FROM paiements WHERE id_reservation = ?")
                    .setParameter(1, id)
                    .executeUpdate();
                System.out.println("    - Paiements supprimés: " + paiementsSupprimes);
                
                // 2. Supprimer la réservation
                int reservationsSupprimees = em.createNativeQuery(
                    "DELETE FROM reservations WHERE id_reservation = ?")
                    .setParameter(1, id)
                    .executeUpdate();
                System.out.println("    - Réservations supprimées: " + reservationsSupprimees);
                
                // 3. Réactiver les contraintes
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                
                // 4. Forcer l'exécution
                em.flush();
                em.clear();
                
                if (reservationsSupprimees > 0) {
                    System.out.println("✅ SUPPRESSION SQL RÉUSSIE");
                } else {
                    System.out.println("⚠️ Aucune réservation supprimée avec SQL");
                }
                
            } catch (Exception e2) {
                System.err.println("❌ ÉCHEC COMPLET: " + e2.getMessage());
                
                // Réactiver les contraintes en cas d'erreur
                try {
                    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                } catch (Exception ignore) {}
                
                throw new RuntimeException("Impossible de supprimer la réservation ID " + id, e);
            }
        }
    }
    
    @Override
    public Reservation getReservationById(Long id) {
        try {
            // Charger les relations principales
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.idReservation = :id";
            
            TypedQuery<Reservation> query = em.createQuery(jpql, Reservation.class)
                    .setParameter("id", id);
            
            List<Reservation> results = query.getResultList();
            Reservation reservation = results.isEmpty() ? null : results.get(0);
            
            // Log pour débogage
            if (reservation != null) {
                System.out.println("🔍 Réservation chargée ID " + id + 
                                 " - Client: " + (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A") +
                                 " - Paiement: " + (reservation.getPaiement() != null ? 
                                     "OUI (ID: " + reservation.getPaiement().getIdPaiement() + ")" : "NON"));
            }
            
            return reservation;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationById: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Reservation> getAllReservations() {
        try {
            // Charger les relations principales
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "ORDER BY r.dateReservation DESC";
            
            return em.createQuery(jpql, Reservation.class).getResultList();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getAllReservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Reservation> getReservationsMars2025() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2025, Calendar.MARCH, 1, 0, 0, 0);
            Date debut = cal.getTime();
            
            cal.set(2025, Calendar.MARCH, 31, 23, 59, 59);
            Date fin = cal.getTime();
            
            // Charger les relations principales
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.dateReservation BETWEEN :debut AND :fin " +
                         "ORDER BY r.dateReservation";
            
            return em.createQuery(jpql, Reservation.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsMars2025: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public void deleteReservationsAvantDate(Date date) {
        try {
            System.out.println("🗑️ Suppression des réservations avant: " + date);
            
            // D'abord charger les réservations avec leurs dépendances
            String selectJpql = "SELECT DISTINCT r FROM Reservation r " +
                               "LEFT JOIN FETCH r.client " +
                               "LEFT JOIN FETCH r.terrain " +
                               "WHERE r.dateReservation < :date";
            
            List<Reservation> reservations = em.createQuery(selectJpql, Reservation.class)
                                             .setParameter("date", date)
                                             .getResultList();
            
            int count = reservations.size();
            System.out.println("📊 " + count + " réservation(s) à supprimer");
            
            // Supprimer chaque réservation proprement
            for (Reservation reservation : reservations) {
                deleteReservation(reservation.getIdReservation());
            }
            
            System.out.println("✅ " + count + " réservation(s) supprimée(s) avant " + date);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur deleteReservationsAvantDate: " + e.getMessage());
            
            // Méthode alternative: suppression directe (moins propre)
            try {
                System.out.println("🔄 Tentative avec suppression directe...");
                int deleted = em.createQuery("DELETE FROM Reservation r WHERE r.dateReservation < :date")
                               .setParameter("date", date)
                               .executeUpdate();
                System.out.println("✅ " + deleted + " réservation(s) supprimée(s) (méthode alternative)");
                
            } catch (Exception e2) {
                System.err.println("❌ Échec des deux méthodes");
                throw new RuntimeException("Impossible de supprimer les réservations", e);
            }
        }
    }
    
    @Override
    @Transactional
    public void updateModePaiementReservation(Long idReservation, String modePaiement) {
        try {
            // Utiliser notre méthode qui charge toutes les relations
            Reservation reservation = getReservationById(idReservation);
            
            if (reservation != null) {
                // VALIDATION: Ne pas accepter "non payé" comme mode de paiement valide
                if (modePaiement == null || modePaiement.trim().isEmpty() || "non payé".equals(modePaiement)) {
                    System.out.println("⚠️ Mode de paiement invalide, conservation du statut actuel: " + 
                        reservation.getModePaiement());
                } else {
                    reservation.setModePaiement(modePaiement);
                    em.merge(reservation);
                    em.flush(); // Forcer la mise à jour
                    System.out.println("✅ Mode paiement mis à jour pour réservation ID: " + idReservation + 
                                      " -> " + modePaiement);
                }
            } else {
                System.out.println("⚠️ Réservation non trouvée ID: " + idReservation);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur updateModePaiementReservation: " + e.getMessage());
            throw new RuntimeException("Erreur mise à jour mode paiement", e);
        }
    }
    
    @Override
    public List<Reservation> getReservationsClient(Long idClient, Date debut, Date fin) {
        try {
            // CORRECTION: Utiliser JOIN (pas FETCH) pour le client dans WHERE
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "JOIN r.client c " +  // JOIN normal (pas FETCH) pour pouvoir utiliser dans WHERE
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE c.idClient = :idClient " +
                         "AND r.dateReservation BETWEEN :debut AND :fin " +
                         "ORDER BY r.dateReservation DESC";
            
            return em.createQuery(jpql, Reservation.class)
                    .setParameter("idClient", idClient)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsClient: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Reservation> getReservationsParDate(Date date) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date debut = cal.getTime();
            
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date fin = cal.getTime();
            
            // Charger les relations principales
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.dateReservation BETWEEN :debut AND :fin " +
                         "ORDER BY r.heureDebut";
            
            return em.createQuery(jpql, Reservation.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsParDate: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public void updateModePaiementReservationById(Long idReservation, String modePaiement) {
        try {
            System.out.println("🔄 Mise à jour mode paiement réservation ID: " + idReservation + " -> " + modePaiement);
            
            // Validation
            if (modePaiement == null || modePaiement.trim().isEmpty() || "non payé".equals(modePaiement)) {
                throw new IllegalArgumentException("Mode de paiement invalide: '" + modePaiement + "'");
            }
            
            // Méthode directe avec JPQL
            String jpql = "UPDATE Reservation r SET r.modePaiement = :modePaiement " +
                         "WHERE r.idReservation = :id";
            
            int updated = em.createQuery(jpql)
                           .setParameter("modePaiement", modePaiement)
                           .setParameter("id", idReservation)
                           .executeUpdate();
            
            em.flush();
            
            if (updated > 0) {
                System.out.println("✅ " + updated + " réservation(s) mise(s) à jour");
            } else {
                System.out.println("⚠️ Aucune réservation trouvée avec ID: " + idReservation);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur updateModePaiementReservationById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur mise à jour mode paiement", e);
        }
    }
    
    @Override
    public void updateModePaiementReservation202(String modePaiement) {
        // Appeler la méthode avec validation
        if (modePaiement != null && !"non payé".equals(modePaiement)) {
            updateModePaiementReservationById(202L, modePaiement);
        } else {
            System.out.println("⚠️ Mode de paiement invalide pour updateModePaiementReservation202: " + modePaiement);
        }
    }
    
    // MÉTHODES CRITIQUES CORRIGÉES
    
    /**
     * Vérifie si une réservation existe
     */
    public boolean reservationExists(Long id) {
        try {
            String jpql = "SELECT COUNT(r) FROM Reservation r WHERE r.idReservation = :id";
            Long count = em.createQuery(jpql, Long.class)
                          .setParameter("id", id)
                          .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Supprime toutes les réservations d'un client
     */
    @Transactional
    public int deleteReservationsByClient(Long clientId) {
        try {
            System.out.println("🗑️ Suppression des réservations du client ID: " + clientId);
            
            Date debut = new Date(0); // Date très ancienne (1er janvier 1970)
            Date fin = new Date();     // Date actuelle
            
            List<Reservation> reservations = getReservationsClient(clientId, debut, fin);
            
            int count = 0;
            for (Reservation reservation : reservations) {
                deleteReservation(reservation.getIdReservation());
                count++;
            }
            
            System.out.println("✅ " + count + " réservation(s) supprimée(s) pour client ID: " + clientId);
            return count;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur deleteReservationsByClient: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Vérifie si une réservation est payée
     */
    public boolean isReservationPayee(Long idReservation) {
        try {
            Reservation reservation = getReservationById(idReservation);
            if (reservation != null) {
                return !"non payé".equals(reservation.getModePaiement());
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Erreur isReservationPayee: " + e.getMessage());
            return false;
        }
    }
    @Transactional
    public void synchroniserToutesReservationsAvecPaiements() {
        try {
            System.out.println("\n🔄 SYNCHRONISATION COMPLÈTE RÉSERVATIONS/PAIEMENTS");
            
            // Récupérer toutes les réservations
            String jpqlReservations = "SELECT r FROM Reservation r";
            List<Reservation> reservations = em.createQuery(jpqlReservations, Reservation.class).getResultList();
            
            System.out.println("📊 Réservations à traiter: " + reservations.size());
            
            int misesAJour = 0;
            int erreurs = 0;
            
            for (Reservation reservation : reservations) {
                try {
                    // Chercher le dernier paiement pour cette réservation
                    String jpqlPaiement = "SELECT p FROM Paiement p " +
                                         "WHERE p.reservation.idReservation = :idReservation " +
                                         "ORDER BY p.datePaiement DESC";
                    
                    List<Paiement> paiements = em.createQuery(jpqlPaiement, Paiement.class)
                                               .setParameter("idReservation", reservation.getIdReservation())
                                               .setMaxResults(1)
                                               .getResultList();
                    
                    if (!paiements.isEmpty()) {
                        Paiement paiement = paiements.get(0);
                        String nouveauMode = paiement.getModePaiement();
                        
                        // Vérifier si besoin de mise à jour
                        if (!nouveauMode.equals(reservation.getModePaiement())) {
                            System.out.println("🔄 Réservation " + reservation.getIdReservation() + 
                                             " : '" + reservation.getModePaiement() + 
                                             "' → '" + nouveauMode + "'");
                            
                            reservation.setModePaiement(nouveauMode);
                            misesAJour++;
                        }
                    } else {
                        // Aucun paiement trouvé, forcer "non payé"
                        if (!"non payé".equals(reservation.getModePaiement())) {
                            System.out.println("🔄 Réservation " + reservation.getIdReservation() + 
                                             " : '" + reservation.getModePaiement() + "' → 'non payé'");
                            
                            reservation.setModePaiement("non payé");
                            misesAJour++;
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ Erreur réservation " + reservation.getIdReservation() + ": " + e.getMessage());
                    erreurs++;
                }
            }
            
            em.flush();
            System.out.println("\n✅ SYNCHRONISATION TERMINÉE");
            System.out.println("   - Mises à jour: " + misesAJour);
            System.out.println("   - Erreurs: " + erreurs);
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR SYNCHRONISATION: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Marque une réservation comme payée avec un mode de paiement spécifique
     */
    @Transactional
    public void marquerCommePayee(Long idReservation, String modePaiement, Double montant) {
        try {
            Reservation reservation = getReservationById(idReservation);
            
            if (reservation != null) {
                // Créer un paiement associé
                Paiement paiement = new Paiement();
                paiement.setMontant(montant != null ? montant : 0.0);
                paiement.setModePaiement(modePaiement);
                paiement.setDatePaiement(new Date());
                paiement.setReservation(reservation);
                paiement.setClient(reservation.getClient());
                
                // Persister le paiement
                em.persist(paiement);
                
                // Mettre à jour la réservation
                reservation.setModePaiement(modePaiement);
                reservation.setPaiement(paiement);
                
                em.merge(reservation);
                em.flush();
                
                System.out.println("✅ Réservation ID " + idReservation + " marquée comme payée avec " + 
                                 modePaiement + " (montant: " + montant + ")");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur marquerCommePayee: " + e.getMessage());
            throw new RuntimeException("Erreur lors du marquage de la réservation comme payée", e);
        }
    }
    
    /**
     * Récupère les réservations non payées
     */
    @Override
    public List<Reservation> getReservationsNonPayees() {
        try {
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.modePaiement = 'non payé' " +
                         "ORDER BY r.dateReservation DESC";
            
            return em.createQuery(jpql, Reservation.class).getResultList();
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsNonPayees: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère les réservations payées - MÉTHODE CRITIQUE CORRIGÉE
     * Cette méthode est utilisée par la page modifierPaiement.jsp
     */
    @Override
    public List<Reservation> getReservationsPayees() {
        try {
            // CORRECTION: Pas d'alias avec FETCH JOIN
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "WHERE r.modePaiement IS NOT NULL " +
                         "AND r.modePaiement != 'non payé' " +
                         "AND r.modePaiement != '' " +
                         "ORDER BY r.dateReservation DESC";
            
            List<Reservation> result = em.createQuery(jpql, Reservation.class).getResultList();
            
            // Log détaillé pour débogage
            System.out.println("📊 Réservations payées trouvées: " + result.size());
            for (Reservation r : result) {
                System.out.println("  - ID: " + r.getIdReservation() + 
                                 ", Client: " + (r.getClient() != null ? r.getClient().getNom() : "null") +
                                 ", Mode: " + r.getModePaiement());
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsPayees: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Méthode alternative pour récupérer les réservations payées avec filtrage supplémentaire
     * pour s'assurer que le client et le terrain sont chargés
     */
    public List<Reservation> getReservationsPayeesAvecDetails() {
        try {
            // CORRECTION: Pas d'alias avec FETCH JOIN
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "LEFT JOIN FETCH r.paiement " +
                         "WHERE r.modePaiement IS NOT NULL " +
                         "AND (r.modePaiement = 'carte' OR r.modePaiement = 'especes' OR r.modePaiement = 'virement') " +
                         "ORDER BY r.dateReservation DESC";
            
            return em.createQuery(jpql, Reservation.class).getResultList();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationsPayeesAvecDetails: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Méthode pour charger une réservation avec TOUTES ses relations (client, terrain, paiement)
     * Utile pour la page de modification de paiement
     */
    public Reservation getReservationAvecToutesRelations(Long idReservation) {
        try {
            String jpql = "SELECT DISTINCT r FROM Reservation r " +
                         "LEFT JOIN FETCH r.client " +
                         "LEFT JOIN FETCH r.terrain " +
                         "LEFT JOIN FETCH r.paiement " +
                         "WHERE r.idReservation = :id";
            
            TypedQuery<Reservation> query = em.createQuery(jpql, Reservation.class)
                    .setParameter("id", idReservation);
            
            List<Reservation> results = query.getResultList();
            Reservation reservation = results.isEmpty() ? null : results.get(0);
            
            if (reservation != null) {
                System.out.println("🔍 Réservation complète chargée ID " + idReservation + 
                                 " - Client: " + (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A") +
                                 " - Paiement: " + (reservation.getPaiement() != null ? "OUI" : "NON"));
            }
            
            return reservation;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getReservationAvecToutesRelations: " + e.getMessage());
            return null;
        }
    }
}