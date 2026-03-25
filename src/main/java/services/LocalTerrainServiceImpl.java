package services;

import entities.Terrain;
import entities.Reservation;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class LocalTerrainServiceImpl implements ILocalTerrainService {
    
    @PersistenceContext(unitName = "gestionTerrainPU")
    private EntityManager em;
    
    @Override
    public void addTerrain(Terrain terrain) {
        em.persist(terrain);
    }
    
    @Override
    public void updateTerrain(Terrain terrain) {
        em.merge(terrain);
    }
    
    @Override
    @Transactional
    public void deleteTerrain(Long id) {
        try {
            System.out.println("\n=== DÉBUT SUPPRESSION TERRAIN ID: " + id + " ===");
            
            // ÉTAPE 1: Trouver et charger le terrain AVEC ses réservations
            String jpql = "SELECT t FROM Terrain t " +
                         "LEFT JOIN FETCH t.reservations " +
                         "WHERE t.idTerrain = :id";
            
            Terrain terrain = em.createQuery(jpql, Terrain.class)
                              .setParameter("id", id)
                              .getResultStream()
                              .findFirst()
                              .orElse(null);
            
            if (terrain == null) {
                System.out.println("✗ Terrain non trouvé avec ID: " + id);
                return;
            }
            
            System.out.println("✓ Terrain trouvé: " + terrain.getNom());
            
            // ÉTAPE 2: Gérer les réservations associées
            if (terrain.getReservations() != null && !terrain.getReservations().isEmpty()) {
                System.out.println("📊 Nombre de réservations à supprimer: " + terrain.getReservations().size());
                
                // Créer une copie de la liste pour éviter ConcurrentModificationException
                List<Reservation> reservationsCopy = new ArrayList<>(terrain.getReservations());
                
                // Supprimer chaque réservation
                for (Reservation reservation : reservationsCopy) {
                    System.out.println("  - Suppression réservation ID: " + reservation.getIdReservation());
                    
                    // IMPORTANT: D'abord dissocier la réservation
                    reservation.setTerrain(null);
                    
                    // Si la réservation a un client, le dissocier aussi
                    if (reservation.getClient() != null) {
                        reservation.getClient().getReservations().remove(reservation);
                        reservation.setClient(null);
                    }
                    
                    // Supprimer la réservation
                    em.remove(reservation);
                }
                
                // Vider la liste des réservations du terrain
                terrain.getReservations().clear();
            }
            
            // ÉTAPE 3: Supprimer le terrain
            em.remove(terrain);
            
            // ÉTAPE 4: Forcer la synchronisation avec la base
            em.flush();
            em.clear(); // Nettoyer le cache pour éviter les problèmes
            
            System.out.println("✅ TERRAIN SUPPRIMÉ AVEC SUCCÈS");
            System.out.println("=== FIN SUPPRESSION TERRAIN ID: " + id + " ===\n");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR lors de la suppression du terrain ID " + id);
            System.err.println("Message: " + e.getMessage());
            
            // Réessayer avec une méthode plus simple
            try {
                System.out.println("🔄 Tentative avec méthode alternative...");
                
                // Supprimer directement avec JPQL (cascade gérée par la base)
                int deleted = em.createQuery("DELETE FROM Terrain t WHERE t.idTerrain = :id")
                               .setParameter("id", id)
                               .executeUpdate();
                
                if (deleted > 0) {
                    System.out.println("✅ Terrain supprimé (méthode alternative)");
                } else {
                    throw new RuntimeException("Aucun terrain supprimé");
                }
                
            } catch (Exception e2) {
                System.err.println("❌ Échec de la méthode alternative: " + e2.getMessage());
                throw new RuntimeException("Impossible de supprimer le terrain ID " + id + ": " + e.getMessage(), e);
            }
        }
    }
    
    @Override
    public Terrain getTerrainById(Long id) {
        String jpql = "SELECT DISTINCT t FROM Terrain t " +
                     "LEFT JOIN FETCH t.reservations " +
                     "WHERE t.idTerrain = :id";
        
        List<Terrain> result = em.createQuery(jpql, Terrain.class)
                               .setParameter("id", id)
                               .setMaxResults(1)  // Optimisation
                               .getResultList();
        
        return result.isEmpty() ? null : result.get(0);
    }
    
    @Override
    public List<Terrain> getAllTerrains() {
        String jpql = "SELECT DISTINCT t FROM Terrain t " +
                     "LEFT JOIN FETCH t.reservations " +
                     "ORDER BY t.nom";
        
        List<Terrain> terrains = em.createQuery(jpql, Terrain.class).getResultList();
        return terrains != null ? terrains : new ArrayList<>();
    }
    
    @Override
    public List<Object[]> getTerrainsNonReserves() {
        try {
            String jpql = "SELECT t.nom, t.localisation " +
                         "FROM Terrain t " +
                         "WHERE NOT EXISTS (" +
                         "    SELECT 1 FROM Reservation r " +
                         "    WHERE r.terrain.idTerrain = t.idTerrain" +
                         ") " +
                         "ORDER BY t.nom";
            
            List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur dans getTerrainsNonReserves: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getReservationsParTerrain() {
        try {
            String jpql = "SELECT t.nom, t.idTerrain, COUNT(r.idReservation) as nbReservations " +
                         "FROM Terrain t " +
                         "LEFT JOIN t.reservations r " +
                         "GROUP BY t.idTerrain, t.nom " +
                         "ORDER BY t.nom";
            
            List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur dans getReservationsParTerrain: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Terrain> rechercherTerrainsParType(String type) {
        try {
            String jpql = "SELECT DISTINCT t FROM Terrain t " +
                         "LEFT JOIN FETCH t.reservations " +
                         "WHERE LOWER(t.type) LIKE LOWER(:type) " +
                         "ORDER BY t.nom";
            
            List<Terrain> result = em.createQuery(jpql, Terrain.class)
                    .setParameter("type", "%" + type + "%")
                    .getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur dans rechercherTerrainsParType: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Terrain> rechercherTerrainsParLocalisation(String localisation) {
        try {
            String jpql = "SELECT DISTINCT t FROM Terrain t " +
                         "LEFT JOIN FETCH t.reservations " +
                         "WHERE LOWER(t.localisation) LIKE LOWER(:localisation) " +
                         "ORDER BY t.nom";
            
            List<Terrain> result = em.createQuery(jpql, Terrain.class)
                    .setParameter("localisation", "%" + localisation + "%")
                    .getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur dans rechercherTerrainsParLocalisation: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getTerrainsAvecNombreReservationsFormate() {
        List<Object[]> resultats = getReservationsParTerrain();
        
        // Formater les résultats pour affichage
        for (Object[] row : resultats) {
            if (row[2] instanceof Long) {
                Long count = (Long) row[2];
                row[2] = count + " réservation" + (count != 1 ? "s" : "");
            }
        }
        
        return resultats;
    }
    
    // NOUVELLE MÉTHODE UTILE: Vérifier si un terrain a des réservations
    public boolean hasReservations(Long terrainId) {
        try {
            String jpql = "SELECT COUNT(r) FROM Reservation r " +
                         "WHERE r.terrain.idTerrain = :terrainId";
            
            Long count = em.createQuery(jpql, Long.class)
                          .setParameter("terrainId", terrainId)
                          .getSingleResult();
            
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Erreur dans hasReservations: " + e.getMessage());
            return false;
        }
    }
    
}