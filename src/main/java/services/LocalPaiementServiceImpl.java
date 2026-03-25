package services;

import entities.Paiement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@Stateless
@Transactional
public class LocalPaiementServiceImpl implements ILocalPaiementService {

    @PersistenceContext(unitName = "gestionTerrainPU") 
    private EntityManager em;

    @Override
    @Transactional
    public void addPaiement(Paiement paiement) {
        try {
            System.out.println("🔄 Ajout paiement...");
            
            // Vérifier et attacher les relations
            if (paiement.getClient() != null && paiement.getClient().getIdClient() != null) {
                paiement.setClient(em.merge(paiement.getClient()));
            }
            
            if (paiement.getReservation() != null && paiement.getReservation().getIdReservation() != null) {
                paiement.setReservation(em.merge(paiement.getReservation()));
            }
            
            em.persist(paiement);
            em.flush(); // Forcer la synchronisation
            
            System.out.println("✅ Paiement ajouté ID: " + paiement.getIdPaiement() + 
                             " - Montant: " + paiement.getMontant() + 
                             " - Client: " + (paiement.getClient() != null ? paiement.getClient().getNom() : "N/A"));
            
        } catch (Exception e) {
            System.err.println("❌ Erreur addPaiement: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur ajout paiement: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updatePaiement(Paiement paiement) {
        try {
            System.out.println("🔄 Mise à jour paiement ID: " + paiement.getIdPaiement());
            
            Paiement managedPaiement = getPaiementById(paiement.getIdPaiement());
            if (managedPaiement != null) {
                managedPaiement.setMontant(paiement.getMontant());
                managedPaiement.setDatePaiement(paiement.getDatePaiement());
                managedPaiement.setModePaiement(paiement.getModePaiement());
                
                if (paiement.getClient() != null) {
                    managedPaiement.setClient(em.merge(paiement.getClient()));
                }
                if (paiement.getReservation() != null) {
                    managedPaiement.setReservation(em.merge(paiement.getReservation()));
                }
                
                em.merge(managedPaiement);
                em.flush();
                System.out.println("✅ Paiement mis à jour ID: " + managedPaiement.getIdPaiement());
            } else {
                throw new RuntimeException("Paiement non trouvé pour mise à jour");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur updatePaiement: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur mise à jour paiement: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deletePaiement(Long id) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔥 SUPPRESSION PAIEMENT ID: " + id);
        
        try {
            // ÉTAPE 1: Vérifier l'existence du paiement
            System.out.println("🔍 Vérification existence paiement...");
            boolean exists = paiementExists(id);
            
            if (!exists) {
                System.out.println("⚠️ Paiement non trouvé avec ID: " + id);
                return;
            }
            
            // ÉTAPE 2: Utiliser la méthode simpleDelete qui contourne les contraintes
            System.out.println("🔄 Utilisation de simpleDeletePaiement...");
            boolean success = simpleDeletePaiement(id);
            
            if (success) {
                System.out.println("✅ Paiement supprimé avec succès !");
            } else {
                System.out.println("❌ Échec de simpleDelete, essai de forceDelete...");
                success = forceDeletePaiement(id);
                
                if (success) {
                    System.out.println("✅ Paiement supprimé avec forceDelete !");
                } else {
                    System.out.println("❌ Échec complet, essai de méthode alternative...");
                    success = deletePaiementAlternative(id);
                }
            }
            
            // ÉTAPE 3: Vérification finale
            System.out.println("🔍 Vérification finale...");
            Thread.sleep(300);
            boolean stillExists = paiementExists(id);
            
            if (!stillExists) {
                System.out.println("✅ VÉRIFICATION: Paiement supprimé avec succès !");
            } else {
                System.out.println("❌ VÉRIFICATION: Paiement toujours présent");
                System.out.println("⚠️ Tentative de méthode radicale...");
                deletePaiementRadical(id);
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR GÉNÉRALE SUPPRESSION PAIEMENT: " + e.getMessage());
            e.printStackTrace();
            
            // Dernière tentative
            try {
                deletePaiementRadical(id);
            } catch (Exception e2) {
                throw new RuntimeException("Impossible de supprimer le paiement ID: " + id, e);
            }
        }
        
        System.out.println("=".repeat(50));
    }
    
    /**
     * Méthode simplifiée pour supprimer un paiement
     */
    @Transactional
    public boolean simpleDeletePaiement(Long id) {
        try {
            System.out.println("🔄 Simple delete paiement ID: " + id);
            
            // 1. D'abord, détacher toutes les réservations qui référencent ce paiement
            int reservationsUpdated = em.createNativeQuery(
                "UPDATE reservations SET id_paiement = NULL, mode_paiement = 'non payé' " +
                "WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            System.out.println("📊 Réservations mises à jour: " + reservationsUpdated);
            
            // 2. Ensuite, supprimer le paiement
            int paiementsDeleted = em.createNativeQuery(
                "DELETE FROM paiements WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            System.out.println("📊 Paiements supprimés: " + paiementsDeleted);
            
            // 3. Forcer la synchronisation
            em.flush();
            em.clear();
            
            return paiementsDeleted > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur simpleDeletePaiement: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Méthode alternative de suppression
     */
    @Transactional
    private boolean deletePaiementAlternative(Long id) {
        try {
            System.out.println("🔄 Méthode alternative pour paiement ID: " + id);
            
            // Désactiver temporairement les contraintes (MySQL)
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            // 1. Supprimer toutes les références dans les réservations
            int reservationsUpdated = em.createNativeQuery(
                "UPDATE reservations SET id_paiement = NULL WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            // 2. Supprimer le paiement
            int paiementsDeleted = em.createNativeQuery(
                "DELETE FROM paiements WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            // Réactiver les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            // Synchroniser
            em.flush();
            em.clear();
            
            System.out.println("📊 Alternative - Réservations: " + reservationsUpdated + 
                             ", Paiements: " + paiementsDeleted);
            
            return paiementsDeleted > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur deletePaiementAlternative: " + e.getMessage());
            
            // Essayer de réactiver les contraintes en cas d'erreur
            try {
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ignore) {}
            
            return false;
        }
    }
    
    /**
     * Méthode radicale pour supprimer un paiement (en cas d'échec des autres méthodes)
     */
    @Transactional
    private void deletePaiementRadical(Long id) {
        try {
            System.out.println("💥 MÉTHODE RADICALE pour paiement ID: " + id);
            
            // Désactiver complètement les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            em.createNativeQuery("SET UNIQUE_CHECKS = 0").executeUpdate();
            
            // Suppression directe sans vérification
            em.createNativeQuery("DELETE FROM paiements WHERE id_paiement = " + id)
              .executeUpdate();
            
            // Nettoyer les réservations orphelines
            em.createNativeQuery(
                "UPDATE reservations SET id_paiement = NULL, mode_paiement = 'non payé' " +
                "WHERE id_paiement = " + id)
              .executeUpdate();
            
            // Réactiver les vérifications
            em.createNativeQuery("SET UNIQUE_CHECKS = 1").executeUpdate();
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            // Synchronisation agressive
            em.flush();
            em.clear();
            
            System.out.println("✅ Suppression radicale effectuée");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur deletePaiementRadical: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Paiement getPaiementById(Long id) {
        try {
            String jpql = "SELECT p FROM Paiement p " +
                         "LEFT JOIN FETCH p.client " +
                         "LEFT JOIN FETCH p.reservation " +
                         "WHERE p.idPaiement = :id";
            
            TypedQuery<Paiement> query = em.createQuery(jpql, Paiement.class)
                                         .setParameter("id", id);
            
            List<Paiement> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getPaiementById: " + e.getMessage());
            return em.find(Paiement.class, id);
        }
    }

    @Override
    public List<Paiement> getAllPaiements() {
        try {
            String jpql = "SELECT p FROM Paiement p " +
                         "LEFT JOIN FETCH p.client " +
                         "LEFT JOIN FETCH p.reservation " +
                         "ORDER BY p.datePaiement DESC";
            
            TypedQuery<Paiement> query = em.createQuery(jpql, Paiement.class);
            return query.getResultList();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur getAllPaiements: " + e.getMessage());
            TypedQuery<Paiement> query = em.createQuery(
                "SELECT p FROM Paiement p ORDER BY p.datePaiement DESC", 
                Paiement.class
            );
            return query.getResultList();
        }
    }

    @Override
    public List<Paiement> getPaiementsParClient(Long idClient) {
        try {
            String jpql = "SELECT p FROM Paiement p " +
                         "LEFT JOIN FETCH p.client c " +
                         "LEFT JOIN FETCH p.reservation " +
                         "WHERE c.idClient = :idClient " +
                         "ORDER BY p.datePaiement DESC";
            
            TypedQuery<Paiement> query = em.createQuery(jpql, Paiement.class)
                    .setParameter("idClient", idClient);
            return query.getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getPaiementsParClient: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Paiement> getPaiementsParReservation(Long idReservation) {
        try {
            String jpql = "SELECT p FROM Paiement p " +
                         "LEFT JOIN FETCH p.client " +
                         "LEFT JOIN FETCH p.reservation r " +
                         "WHERE r.idReservation = :idReservation " +
                         "ORDER BY p.datePaiement";
            
            TypedQuery<Paiement> query = em.createQuery(jpql, Paiement.class)
                    .setParameter("idReservation", idReservation);
            return query.getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getPaiementsParReservation: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    @Transactional
    public boolean forceDeletePaiement(Long id) {
        try {
            System.out.println("💥 FORCE DELETE PAIEMENT ID: " + id);
            
            // Désactiver toutes les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            // 1. Supprimer d'abord les références
            em.createNativeQuery(
                "UPDATE reservations SET id_paiement = NULL WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            // 2. Supprimer le paiement
            int deleted = em.createNativeQuery(
                "DELETE FROM paiements WHERE id_paiement = ?")
                .setParameter(1, id)
                .executeUpdate();
            
            // Réactiver les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            // Synchronisation
            em.flush();
            em.clear();
            
            System.out.println("📊 Force delete - Supprimés: " + deleted);
            return deleted > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur forceDeletePaiement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public void debugPaiement(Long id) {
        try {
            System.out.println("\n🔍 DEBUG PAIEMENT ID: " + id);
            
            // Vérifier avec SQL direct
            @SuppressWarnings("unchecked")
            List<Object[]> results = em.createNativeQuery(
                "SELECT p.id_paiement, p.montant, p.mode_paiement, " +
                "r.id_reservation, r.mode_paiement as statut_reservation " +
                "FROM paiements p " +
                "LEFT JOIN reservations r ON r.id_paiement = p.id_paiement " +
                "WHERE p.id_paiement = ?")
                .setParameter(1, id)
                .getResultList();
            
            System.out.println("📊 Résultats SQL: " + results.size() + " ligne(s)");
            
            for (Object[] row : results) {
                System.out.println("   - ID Paiement: " + row[0]);
                System.out.println("   - Montant: " + row[1]);
                System.out.println("   - Mode: " + row[2]);
                System.out.println("   - ID Réservation: " + row[3]);
                System.out.println("   - Statut Réservation: " + row[4]);
            }
            
            // Vérification supplémentaire
            @SuppressWarnings("unchecked")
            List<Object[]> paiementDirect = em.createNativeQuery(
                "SELECT COUNT(*) FROM paiements WHERE id_paiement = ?")
                .setParameter(1, id)
                .getResultList();
            
            long count = paiementDirect.isEmpty() ? 0 : ((Number) paiementDirect.get(0)[0]).longValue();
            System.out.println("📊 Paiement dans table paiements: " + count);
            
            @SuppressWarnings("unchecked")
            List<Object[]> reservations = em.createNativeQuery(
                "SELECT COUNT(*) FROM reservations WHERE id_paiement = ?")
                .setParameter(1, id)
                .getResultList();
            
            long reservationCount = reservations.isEmpty() ? 0 : ((Number) reservations.get(0)[0]).longValue();
            System.out.println("📊 Réservations liées: " + reservationCount);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur debugPaiement: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean canDeletePaiement(Long id) {
        try {
            // Toujours permettre la suppression
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur canDeletePaiement: " + e.getMessage());
            return true;
        }
    }
    
    @Override
    public Double getTotalPaiementsClient(Long idClient) {
        try {
            String jpql = "SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p " +
                         "WHERE p.client.idClient = :idClient";
            
            TypedQuery<Double> query = em.createQuery(jpql, Double.class)
                    .setParameter("idClient", idClient);
            return query.getSingleResult();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getTotalPaiementsClient: " + e.getMessage());
            return 0.0;
        }
    }
    
    @Override
    public Double getTotalPaiementsReservation(Long idReservation) {
        try {
            String jpql = "SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p " +
                         "WHERE p.reservation.idReservation = :idReservation";
            
            TypedQuery<Double> query = em.createQuery(jpql, Double.class)
                    .setParameter("idReservation", idReservation);
            return query.getSingleResult();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getTotalPaiementsReservation: " + e.getMessage());
            return 0.0;
        }
    }
    
    @Override
    public boolean paiementExists(Long id) {
        try {
            String jpql = "SELECT COUNT(p) FROM Paiement p WHERE p.idPaiement = :id";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                          .setParameter("id", id);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            // Fallback: requête native
            try {
                @SuppressWarnings("unchecked")
                List<Object[]> results = em.createNativeQuery(
                    "SELECT COUNT(*) FROM paiements WHERE id_paiement = ?")
                    .setParameter(1, id)
                    .getResultList();
                
                return !results.isEmpty() && ((Number) results.get(0)[0]).longValue() > 0;
            } catch (Exception e2) {
                return false;
            }
        }
    }
    
    @Override
    @Transactional
    public int deleteAllPaiementsClient(Long idClient) {
        try {
            System.out.println("🗑️ Suppression de tous les paiements du client ID: " + idClient);
            
            // Désactiver temporairement les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            // 1. Détacher les réservations
            em.createNativeQuery(
                "UPDATE reservations SET id_paiement = NULL, mode_paiement = 'non payé' " +
                "WHERE id_paiement IN (SELECT id_paiement FROM paiements WHERE id_client = ?)")
                .setParameter(1, idClient)
                .executeUpdate();
            
            // 2. Supprimer les paiements
            int deleted = em.createNativeQuery(
                "DELETE FROM paiements WHERE id_client = ?")
                .setParameter(1, idClient)
                .executeUpdate();
            
            // Réactiver les contraintes
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            em.flush();
            em.clear();
            
            System.out.println("✅ " + deleted + " paiement(s) supprimé(s)");
            return deleted;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur deleteAllPaiementsClient: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public List<Paiement> getPaiementsParMode(String modePaiement) {
        try {
            String jpql = "SELECT p FROM Paiement p " +
                         "LEFT JOIN FETCH p.client " +
                         "LEFT JOIN FETCH p.reservation " +
                         "WHERE p.modePaiement = :mode " +
                         "ORDER BY p.datePaiement DESC";
            
            TypedQuery<Paiement> query = em.createQuery(jpql, Paiement.class)
                    .setParameter("mode", modePaiement);
            return query.getResultList();
                    
        } catch (Exception e) {
            System.err.println("❌ Erreur getPaiementsParMode: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public long countPaiements() {
        try {
            String jpql = "SELECT COUNT(p) FROM Paiement p";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("❌ Erreur countPaiements: " + e.getMessage());
            return 0;
        }
    }
}