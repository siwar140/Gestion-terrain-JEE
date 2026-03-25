package services;

import entities.Client;
import entities.Reservation;
import entities.Paiement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@Stateless
@Transactional
public class ClientService implements ILocalClientService {
    
    @PersistenceContext(unitName = "gestionTerrainPU")
    private EntityManager em;

    // ========== MÉTHODE PRINCIPALE POUR LE MONTANT TOTAL ==========
    
    @Override
    public List<Object[]> getMontantTotalParClient() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("=== SERVICE: getMontantTotalParClient() ===");
            
            // Essayer d'abord la version V2 (via réservations)
            System.out.println("🔄 Tentative avec V2 (via réservations)...");
            List<Object[]> results = getMontantTotalParClientV2();
            
            if (results.isEmpty()) {
                System.out.println("🔄 V2 vide, tentative avec V1 (direct)...");
                results = getMontantTotalParClientV1();
            }
            
            if (results.isEmpty()) {
                System.out.println("🔄 Les deux versions sont vides, tentative avec SQL natif...");
                results = getMontantTotalParClientNative();
            }
            
            // Debug des résultats
            System.out.println("📊 Résultats obtenus: " + results.size() + " enregistrement(s)");
            
            if (!results.isEmpty()) {
                System.out.println("\n📋 Détail des résultats:");
                double totalGeneral = 0;
                for (Object[] row : results) {
                    double montant = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
                    System.out.println(String.format("  %-20s %-30s %10.2f €", 
                        row[0], row[1], montant));
                    totalGeneral += montant;
                }
                System.out.println("💰 Total général: " + totalGeneral + " €");
            } else {
                System.out.println("⚠️ Aucun résultat trouvé!");
                debugMontantTotal(); // Lancer le debug
            }
            
            System.out.println("=".repeat(60));
            
            return results;
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR FATALE dans getMontantTotalParClient: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    // Version 1: Via relation directe Client-Paiement
    public List<Object[]> getMontantTotalParClientV1() {
        try {
            System.out.println("=== VERSION 1: Relation Client-Paiement directe ===");
            
            String jpql = "SELECT c.nom, c.email, COALESCE(SUM(p.montant), 0) as total " +
                         "FROM Client c " +
                         "LEFT JOIN Paiement p ON p.client.idClient = c.idClient " +
                         "GROUP BY c.idClient, c.nom, c.email " +
                         "HAVING COALESCE(SUM(p.montant), 0) > 0 " +
                         "ORDER BY total DESC, c.nom ASC";
            
            System.out.println("JPQL: " + jpql);
            
            return em.createQuery(jpql, Object[].class).getResultList();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur V1: " + e.getMessage());
            return List.of();
        }
    }
    
    // Version 2: Via réservations (plus probable)
    public List<Object[]> getMontantTotalParClientV2() {
        try {
            System.out.println("=== VERSION 2: Via réservations ===");
            
            String jpql = "SELECT c.nom, c.email, COALESCE(SUM(p.montant), 0) as total " +
                         "FROM Client c " +
                         "LEFT JOIN Reservation r ON r.client.idClient = c.idClient " +
                         "LEFT JOIN Paiement p ON p.reservation.idReservation = r.idReservation " +
                         "WHERE p.montant IS NOT NULL " +
                         "GROUP BY c.idClient, c.nom, c.email " +
                         "ORDER BY total DESC";
            
            System.out.println("JPQL: " + jpql);
            
            return em.createQuery(jpql, Object[].class).getResultList();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur V2: " + e.getMessage());
            return List.of();
        }
    }
    
    // Version SQL natif (en dernier recours)
    @Transactional
    public List<Object[]> getMontantTotalParClientNative() {
        try {
            System.out.println("=== SQL NATIF: getMontantTotalParClient ===");
            
            String nativeSql = "SELECT " +
                              "  c.nom, " +
                              "  c.email, " +
                              "  COALESCE(SUM(p.montant), 0) as total " +
                              "FROM clients c " +
                              "LEFT JOIN paiements p ON p.id_client = c.id_client " +
                              "GROUP BY c.id_client, c.nom, c.email " +
                              "ORDER BY total DESC, c.nom ASC";
            
            System.out.println("SQL: " + nativeSql);
            
            List<Object[]> results = em.createNativeQuery(nativeSql).getResultList();
            System.out.println("✅ SQL réussi: " + results.size() + " résultats");
            
            return results;
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR SQL Natif: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    // ========== MÉTHODE DE DÉBOGAGE ==========
    
    public void debugMontantTotal() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🔍 DEBUG COMPLET: Montant Total par Client");
            System.out.println("=".repeat(60));
            
            // 1. Vérifier la structure des tables
            System.out.println("\n🗄️ Structure des tables:");
            try {
                List<Object[]> tables = em.createNativeQuery(
                    "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME IN ('clients', 'paiements', 'reservations') " +
                    "ORDER BY TABLE_NAME, ORDINAL_POSITION"
                ).getResultList();
                
                for (Object[] table : tables) {
                    System.out.println("  " + table[0] + "." + table[1] + " (" + table[2] + ")");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Impossible de lire la structure des tables");
            }
            
            // 2. Vérifier les paiements
            System.out.println("\n💰 Liste de TOUS les paiements:");
            String jpqlPaiements = "SELECT p.idPaiement, p.montant, " +
                                  "c.nom as client_nom, c.idClient, " +
                                  "r.idReservation " +
                                  "FROM Paiement p " +
                                  "LEFT JOIN p.client c " +
                                  "LEFT JOIN p.reservation r " +
                                  "ORDER BY p.montant DESC";
            
            List<Object[]> allPaiements = em.createQuery(jpqlPaiements, Object[].class).getResultList();
            
            if (allPaiements.isEmpty()) {
                System.out.println("❌ Aucun paiement enregistré!");
            } else {
                double totalGeneral = 0;
                for (Object[] paiement : allPaiements) {
                    System.out.println(String.format("  ID:%-4d Montant:%8.2f Client:%-20s (ID:%-3d) Réservation:%s",
                        paiement[0], 
                        paiement[1] != null ? ((Number) paiement[1]).doubleValue() : 0,
                        paiement[2] != null ? paiement[2] : "NULL",
                        paiement[3] != null ? paiement[3] : "NULL",
                        paiement[4] != null ? paiement[4] : "NULL"));
                    
                    if (paiement[1] != null) {
                        totalGeneral += ((Number) paiement[1]).doubleValue();
                    }
                }
                System.out.println("💰 Total général des paiements: " + totalGeneral + " €");
                System.out.println("📊 Nombre de paiements: " + allPaiements.size());
            }
            
            // 3. Vérifier les clients
            System.out.println("\n👤 Liste de TOUS les clients:");
            List<Client> allClients = getAllClients();
            System.out.println("📊 Nombre total de clients: " + allClients.size());
            
            // 4. Tester différentes requêtes
            System.out.println("\n🧪 Test des requêtes:");
            
            // Test 1: Clients avec paiements
            System.out.println("Test 1: Clients avec paiements (JPQL):");
            String test1 = "SELECT c.nom, COUNT(p) as nb_paiements, SUM(p.montant) as total " +
                         "FROM Client c " +
                         "LEFT JOIN Paiement p ON p.client.idClient = c.idClient " +
                         "GROUP BY c.idClient, c.nom " +
                         "HAVING COUNT(p) > 0";
            
            try {
                List<Object[]> test1Results = em.createQuery(test1, Object[].class).getResultList();
                System.out.println("  Résultats: " + test1Results.size());
                for (Object[] row : test1Results) {
                    System.out.println("  " + row[0] + " - " + row[1] + " paiements - " + row[2] + " €");
                }
            } catch (Exception e) {
                System.out.println("  ❌ Erreur: " + e.getMessage());
            }
            
            // Test 2: Simple comptage
            System.out.println("\nTest 2: Statistiques simples:");
            try {
                Long nbClientsAvecPaiement = em.createQuery(
                    "SELECT COUNT(DISTINCT p.client) FROM Paiement p WHERE p.client IS NOT NULL", 
                    Long.class).getSingleResult();
                System.out.println("  Clients avec paiement(s): " + nbClientsAvecPaiement);
                
                Long nbPaiementsSansClient = em.createQuery(
                    "SELECT COUNT(p) FROM Paiement p WHERE p.client IS NULL", 
                    Long.class).getSingleResult();
                System.out.println("  Paiements sans client: " + nbPaiementsSansClient);
                
            } catch (Exception e) {
                System.out.println("  ❌ Erreur: " + e.getMessage());
            }
            
            System.out.println("=".repeat(60) + "\n");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur debugMontantTotal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== RESTE DES MÉTHODES (inchangées) ==========
    
    @Override
    @Transactional
    public boolean deleteClient(Long clientId) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔴 DÉBUT SUPPRESSION CLIENT ID: " + clientId);
        
        try {
            // ÉTAPE 1: Vérifier si le client existe avec toutes ses relations
            Client client = getClientByIdWithRelations(clientId);
            if (client == null) {
                System.out.println("❌ Client introuvable");
                return false;
            }
            
            System.out.println("✅ Client trouvé: " + client.getNom());
            System.out.println("📊 Email: " + client.getEmail());
            System.out.println("📈 Nombre de réservations: " + 
                (client.getReservations() != null ? client.getReservations().size() : 0));
            System.out.println("📈 Nombre de paiements: " + 
                (client.getPaiements() != null ? client.getPaiements().size() : 0));
            
            // ÉTAPE 2: Essayer d'abord la méthode JPA propre
            if (deleteClientWithJPA(client)) {
                System.out.println("✅ Suppression réussie avec JPA");
                return true;
            }
            
            // ÉTAPE 3: Si échec, utiliser la méthode native
            System.out.println("🔄 Méthode JPA échouée, tentative avec méthode native...");
            return deleteWithNativeTransaction(clientId, client.getNom());
            
        } catch (Exception e) {
            System.err.println("💥 ERREUR FATALE: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            System.out.println("=".repeat(60) + "\n");
        }
    }
    
    @Transactional
    private boolean deleteClientWithJPA(Client client) {
        try {
            System.out.println("🔄 Tentative suppression JPA pour: " + client.getNom());
            
            // 1. Supprimer d'abord les paiements du client
            if (client.getPaiements() != null && !client.getPaiements().isEmpty()) {
                System.out.println("🗑️ Suppression des paiements...");
                // Créer une copie pour éviter ConcurrentModificationException
                List<Paiement> paiementsCopy = List.copyOf(client.getPaiements());
                for (Paiement paiement : paiementsCopy) {
                    // Détacher la relation
                    paiement.setClient(null);
                    paiement.setReservation(null);
                    em.remove(paiement);
                }
                client.getPaiements().clear();
            }
            
            // 2. Supprimer les réservations du client
            if (client.getReservations() != null && !client.getReservations().isEmpty()) {
                System.out.println("🗑️ Suppression des réservations...");
                // Créer une copie
                List<Reservation> reservationsCopy = List.copyOf(client.getReservations());
                for (Reservation reservation : reservationsCopy) {
                    deleteReservationForClient(reservation);
                }
                client.getReservations().clear();
            }
            
            // 3. Détacher complètement avant suppression
            em.detach(client);
            
            // 4. Recharger le client pour être sûr qu'il est managé
            Client managedClient = em.find(Client.class, client.getIdClient());
            if (managedClient != null) {
                em.remove(managedClient);
                em.flush();
                System.out.println("✅ Client supprimé avec JPA");
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ Échec suppression JPA: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    private void deleteReservationForClient(Reservation reservation) {
        try {
            System.out.println("  - Suppression réservation ID: " + reservation.getIdReservation());
            
            if (reservation.getTerrain() != null) {
                reservation.getTerrain().getReservations().remove(reservation);
                em.merge(reservation.getTerrain());
            }
            
            reservation.setClient(null);
            em.remove(reservation);
            
        } catch (Exception e) {
            System.err.println("  ❌ Erreur suppression réservation: " + e.getMessage());
        }
    }
    
    @Transactional
    private boolean deleteWithNativeTransaction(Long clientId, String clientName) {
        System.out.println("🔄 Début transaction native pour: " + clientName);
        
        try {
            System.out.println("🔓 Désactivation des contraintes FOREIGN_KEY_CHECKS");
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            
            try {
                System.out.println("🗑️ Tentative suppression des paiements...");
                int paiementsSupprimes = em.createNativeQuery(
                    "DELETE FROM paiements WHERE id_client = ?")
                    .setParameter(1, clientId)
                    .executeUpdate();
                System.out.println("✅ Paiements supprimés: " + paiementsSupprimes);
            } catch (Exception e) {
                System.out.println("ℹ️ Aucun paiement à supprimer ou table inexistante: " + e.getMessage());
            }
            
            try {
                System.out.println("🗑️ Tentative suppression des réservations...");
                int reservationsSupprimees = em.createNativeQuery(
                    "DELETE FROM reservations WHERE id_client = ?")
                    .setParameter(1, clientId)
                    .executeUpdate();
                System.out.println("✅ Réservations supprimées: " + reservationsSupprimees);
            } catch (Exception e) {
                System.out.println("ℹ️ Aucune réservation à supprimer ou table inexistante: " + e.getMessage());
            }
            
            System.out.println("🗑️ Suppression du client...");
            int clientsSupprimes = em.createNativeQuery(
                "DELETE FROM clients WHERE id_client = ?")
                .setParameter(1, clientId)
                .executeUpdate();
            
            System.out.println("✅ Clients supprimés: " + clientsSupprimes);
            
            System.out.println("🔒 Réactivation des contraintes FOREIGN_KEY_CHECKS");
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            
            em.flush();
            em.clear();
            
            boolean success = clientsSupprimes > 0;
            System.out.println(success ? "🎉 SUPPRESSION RÉUSSIE" : "❌ AUCUN CLIENT SUPPRIMÉ");
            return success;
            
        } catch (Exception e) {
            System.err.println("💥 ERREUR transaction native: " + e.getMessage());
            e.printStackTrace();
            
            try {
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ex) {
                System.err.println("⚠️ Impossible de réactiver FOREIGN_KEY_CHECKS");
            }
            
            return false;
        }
    }
    
    @Transactional
    public boolean deleteClientDirectTest(Long clientId) {
        System.out.println("🧪 TEST DIRECT - Suppression client ID: " + clientId);
        
        try {
            System.out.println("Test 1: JPQL DELETE");
            int deleted = em.createQuery("DELETE FROM Client c WHERE c.idClient = :id")
                .setParameter("id", clientId)
                .executeUpdate();
            
            if (deleted > 0) {
                System.out.println("✅ JPQL a supprimé " + deleted + " client(s)");
                em.flush();
                return true;
            }
            
            System.out.println("Test 2: SQL NATIF");
            deleted = em.createNativeQuery("DELETE FROM clients WHERE id_client = ?")
                .setParameter(1, clientId)
                .executeUpdate();
            
            System.out.println("✅ SQL natif a supprimé " + deleted + " client(s)");
            em.flush();
            return deleted > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur test direct: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public void addClient(Client client) {
        try {
            em.persist(client);
            em.flush();
            System.out.println("✅ Client ajouté: " + client.getNom() + " (ID: " + client.getIdClient() + ")");
        } catch (Exception e) {
            System.err.println("❌ Erreur ajout client: " + e.getMessage());
            throw new RuntimeException("Erreur ajout client", e);
        }
    }

    @Override
    @Transactional
    public void updateClient(Client client) {
        try {
            if (!em.contains(client) && client.getIdClient() != null) {
                Client managedClient = getClientByIdWithRelations(client.getIdClient());
                if (managedClient != null) {
                    managedClient.setNom(client.getNom());
                    managedClient.setEmail(client.getEmail());
                    managedClient.setTelephone(client.getTelephone());
                    managedClient.setDateInscription(client.getDateInscription());
                    
                    em.merge(managedClient);
                    System.out.println("✅ Client modifié: " + client.getNom() + " (ID: " + client.getIdClient() + ")");
                    return;
                }
            }
            em.merge(client);
            System.out.println("✅ Client modifié: " + client.getNom());
        } catch (Exception e) {
            System.err.println("❌ Erreur modification client: " + e.getMessage());
            throw new RuntimeException("Erreur modification client", e);
        }
    }

    @Override
    public Client getClientById(Long id) {
        return getClientByIdWithRelations(id);
    }
    
    private Client getClientByIdWithRelations(Long id) {
        try {
            String jpql = "SELECT DISTINCT c FROM Client c " +
                         "LEFT JOIN FETCH c.reservations " +
                         "LEFT JOIN FETCH c.paiements " +
                         "WHERE c.idClient = :id";
            
            List<Client> results = em.createQuery(jpql, Client.class)
                                   .setParameter("id", id)
                                   .setMaxResults(1)
                                   .getResultList();
            
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            System.err.println("❌ Erreur getClientByIdWithRelations: " + e.getMessage());
            return em.find(Client.class, id);
        }
    }

    @Override
    public List<Client> getAllClients() {
        try {
            String jpql = "SELECT DISTINCT c FROM Client c " +
                         "LEFT JOIN FETCH c.reservations " +
                         "LEFT JOIN FETCH c.paiements " +
                         "ORDER BY c.nom";
            
            return em.createQuery(jpql, Client.class).getResultList();
        } catch (Exception e) {
            System.err.println("❌ Erreur getAllClients: " + e.getMessage());
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c ORDER BY c.nom", 
                Client.class
            );
            return query.getResultList();
        }
    }

    @Override
    public List<Object[]> getClientsFootballTries() {
        try {
            String jpql = "SELECT c.nom, c.email, c.dateInscription " +
                         "FROM Client c " +
                         "WHERE EXISTS (" +
                         "    SELECT 1 FROM Reservation r " +
                         "    WHERE r.client.idClient = c.idClient " +
                         "    AND r.terrain.type = 'Football'" +
                         ") " +
                         "ORDER BY c.dateInscription DESC";
            
            return em.createQuery(jpql, Object[].class).getResultList();
        } catch (Exception e) {
            System.err.println("❌ Erreur getClientsFootballTries: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Client> rechercherClientsParNom(String nom) {
        try {
            String jpql = "SELECT DISTINCT c FROM Client c " +
                         "LEFT JOIN FETCH c.reservations " +
                         "LEFT JOIN FETCH c.paiements " +
                         "WHERE LOWER(c.nom) LIKE LOWER(:nom) " +
                         "ORDER BY c.nom";
            
            return em.createQuery(jpql, Client.class)
                    .setParameter("nom", "%" + nom + "%")
                    .getResultList();
        } catch (Exception e) {
            System.err.println("❌ Erreur rechercherClientsParNom: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Object[]> getHistoriqueReservationsClient(Long idClient) {
        try {
            String jpql = "SELECT r.dateReservation, t.nom, t.type, r.heureDebut, r.duree " +
                         "FROM Reservation r " +
                         "JOIN r.terrain t " +
                         "WHERE r.client.idClient = :idClient " +
                         "ORDER BY r.dateReservation DESC";
            
            return em.createQuery(jpql, Object[].class)
                    .setParameter("idClient", idClient)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("❌ Erreur getHistoriqueReservationsClient: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean deleteClientWithConstraints(Long clientId) {
        return deleteClientDirectTest(clientId);
    }
    
    public boolean canDeleteClient(Long clientId) {
        try {
            String jpql = "SELECT COUNT(r) FROM Reservation r " +
                         "WHERE r.client.idClient = :clientId " +
                         "AND r.dateReservation >= CURRENT_DATE";
            
            Long count = em.createQuery(jpql, Long.class)
                          .setParameter("clientId", clientId)
                          .getSingleResult();
            
            return count == 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur canDeleteClient: " + e.getMessage());
            return false;
        }
    }
    
    public long countClients() {
        try {
            String jpql = "SELECT COUNT(c) FROM Client c";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (Exception e) {
            System.err.println("❌ Erreur countClients: " + e.getMessage());
            return 0;
        }
    }
    
    public Client findClientByEmail(String email) {
        try {
            String jpql = "SELECT DISTINCT c FROM Client c " +
                         "LEFT JOIN FETCH c.reservations " +
                         "LEFT JOIN FETCH c.paiements " +
                         "WHERE c.email = :email";
            
            List<Client> results = em.createQuery(jpql, Client.class)
                                   .setParameter("email", email)
                                   .setMaxResults(1)
                                   .getResultList();
            
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            System.err.println("❌ Erreur findClientByEmail: " + e.getMessage());
            return null;
        }
    }
}