package controllers;

import services.*;
import entities.*;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/main/*")
public class MainServlet extends HttpServlet {
    
    @EJB
    private ILocalClientService clientService;
    
    @EJB
    private ILocalTerrainService terrainService;
    
    @EJB
    private ILocalReservationService reservationService;
    
    @EJB
    private ILocalPaiementService paiementService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        
        switch (path) {
            case "/clients":
                afficherListeClients(request, response);
                break;
            case "/terrains":
                afficherListeTerrains(request, response);
                break;
            case "/reservations":
                afficherListeReservations(request, response);
                break;
            case "/paiements":
                afficherListePaiements(request, response);
                break;
            case "/ajouterClient":
                afficherFormulaireAjoutClient(request, response);
                break;
            case "/ajouterTerrain":
                afficherFormulaireAjoutTerrain(request, response);
                break;
            case "/ajouterReservation":
                afficherFormulaireAjoutReservation(request, response);
                break;
            case "/ajouterPaiement":
                afficherFormulaireAjoutPaiement(request, response);
                break;
            case "/clientsFootball":
                afficherClientsFootball(request, response);
                break;
            case "/montantTotal":
                afficherMontantTotalParClient(request, response);
                break;
            case "/reservationsMars2025":
                afficherReservationsMars2025(request, response);
                break;
            case "/reservationsParTerrain":
                afficherReservationsParTerrain(request, response);
                break;
            case "/terrainsNonReserves":
                afficherTerrainsNonReserves(request, response);
                break;
            case "/supprimerReservationsAvant":
                afficherSupprimerReservationsAvant(request, response);
                break;
            case "/modifierPaiement":
                afficherModifierPaiement(request, response);
                break;
            case "/dashboard":
                afficherDashboard(request, response);
                break;
            case "/testSuppression":
                testerSuppressionReservation(request, response);
                break;
            case "/synchroniserPaiements":
                synchroniserPaiements(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/main/");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if (path == null) {
            response.sendRedirect(request.getContextPath() + "/main/");
            return;
        }
        
        switch (path) {
            case "/ajouterClient":
                traiterAjoutClient(request, response);
                break;
            case "/ajouterTerrain":
                traiterAjoutTerrain(request, response);
                break;
            case "/ajouterReservation":
                traiterAjoutReservation(request, response);
                break;
            case "/ajouterPaiement":
                traiterAjoutPaiement(request, response);
                break;
            case "/supprimerPaiement":
                supprimerPaiement(request, response);
                break;
            case "/supprimerTerrain":
                supprimerTerrain(request, response);
                break;
            case "/supprimerClient":
                supprimerClient(request, response);
                break;
            case "/supprimerReservation":
                supprimerReservation(request, response);
                break;
            case "/executerSuppression":
                executerSupprimerReservationsAvant(request, response);
                break;
            case "/modifierPaiement":
                modifierPaiement(request, response);
                break;
            case "/synchroniserPaiements":
                traiterSynchronisationPaiements(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/main/");
        }
    }
    
    // ========== MÉTHODES DE SYNCHRONISATION ==========
    
    private void synchroniserPaiements(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Reservation> toutesReservations = reservationService.getAllReservations();
            List<Paiement> tousPaiements = paiementService.getAllPaiements();
            
            int reservationsSansPaiement = 0;
            int reservationsAvecPaiement = 0;
            int incohérences = 0;
            
            for (Reservation reservation : toutesReservations) {
                boolean aPaiement = false;
                
                for (Paiement paiement : tousPaiements) {
                    if (paiement.getReservation() != null && 
                        paiement.getReservation().getIdReservation().equals(reservation.getIdReservation())) {
                        aPaiement = true;
                        break;
                    }
                }
                
                if (aPaiement) {
                    reservationsAvecPaiement++;
                    if ("non payé".equals(reservation.getModePaiement())) {
                        incohérences++;
                    }
                } else {
                    reservationsSansPaiement++;
                }
            }
            
            request.setAttribute("totalReservations", toutesReservations.size());
            request.setAttribute("totalPaiements", tousPaiements.size());
            request.setAttribute("reservationsAvecPaiement", reservationsAvecPaiement);
            request.setAttribute("reservationsSansPaiement", reservationsSansPaiement);
            request.setAttribute("incoherences", incohérences);
            
            request.getRequestDispatcher("/synchroniserPaiements.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Erreur lors du chargement des données: " + e.getMessage());
            request.setAttribute("typeMessage", "error");
            response.sendRedirect(request.getContextPath() + "/main/dashboard");
        }
    }
    
    private void traiterSynchronisationPaiements(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début synchronisation paiements ===");
        
        String message = "";
        String typeMessage = "";
        int countCorrigees = 0;
        
        try {
            List<Reservation> toutesReservations = reservationService.getAllReservations();
            List<Paiement> tousPaiements = paiementService.getAllPaiements();
            
            System.out.println("📊 Statistiques initiales:");
            System.out.println("   - Réservations totales: " + toutesReservations.size());
            System.out.println("   - Paiements totaux: " + tousPaiements.size());
            
            for (Reservation reservation : toutesReservations) {
                boolean aPaiement = false;
                Paiement paiementAssocie = null;
                
                for (Paiement paiement : tousPaiements) {
                    if (paiement.getReservation() != null && 
                        paiement.getReservation().getIdReservation().equals(reservation.getIdReservation())) {
                        aPaiement = true;
                        paiementAssocie = paiement;
                        break;
                    }
                }
                
                if (aPaiement && paiementAssocie != null) {
                    if ("non payé".equals(reservation.getModePaiement())) {
                        System.out.println("🔄 Correction réservation ID " + reservation.getIdReservation() + 
                                         ": 'non payé' → '" + paiementAssocie.getModePaiement() + "'");
                        
                        reservation.setModePaiement(paiementAssocie.getModePaiement());
                        reservationService.updateReservation(reservation);
                        countCorrigees++;
                    }
                } else {
                    if (!"non payé".equals(reservation.getModePaiement())) {
                        System.out.println("🔄 Correction réservation ID " + reservation.getIdReservation() + 
                                         ": '" + reservation.getModePaiement() + "' → 'non payé'");
                        
                        reservation.setModePaiement("non payé");
                        reservationService.updateReservation(reservation);
                        countCorrigees++;
                    }
                }
            }
            
            if (countCorrigees > 0) {
                message = "✅ Synchronisation terminée ! " + countCorrigees + " réservation(s) corrigée(s).";
                typeMessage = "success";
                System.out.println("✅ " + message);
            } else {
                message = "ℹ️ Aucune incohérence détectée. Toutes les réservations sont déjà synchronisées.";
                typeMessage = "info";
                System.out.println("ℹ️ " + message);
            }
            
        } catch (Exception e) {
            message = "❌ Erreur lors de la synchronisation: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== SERVLET: Fin synchronisation paiements ===");
        System.out.println("=".repeat(50) + "\n");
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        request.setAttribute("countCorrigees", countCorrigees);
        synchroniserPaiements(request, response);
    }
    
    // ========== MÉTHODE: SUPPRESSION PAIEMENT ==========
    
    private void supprimerPaiement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début suppression paiement ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Méthode: " + request.getMethod());
        System.out.println("Paramètres: " + request.getParameterMap());
        
        String message = "";
        String typeMessage = "";
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("ID reçu: " + idStr);
            
            if (idStr == null || idStr.isEmpty()) {
                message = "❌ ID du paiement non spécifié";
                typeMessage = "error";
                System.out.println("❌ ERREUR: " + message);
            } else {
                Long paiementId = Long.parseLong(idStr);
                System.out.println("ID numérique: " + paiementId);
                
                Paiement paiement = paiementService.getPaiementById(paiementId);
                if (paiement == null) {
                    message = "❌ Paiement introuvable avec ID: " + paiementId;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: " + message);
                } else {
                    System.out.println("📋 Paiement à supprimer:");
                    System.out.println("   - Montant: " + paiement.getMontant());
                    System.out.println("   - Mode: " + paiement.getModePaiement());
                    System.out.println("   - Date: " + paiement.getDatePaiement());
                    System.out.println("   - Client: " + (paiement.getClient() != null ? paiement.getClient().getNom() : "N/A"));
                    System.out.println("   - Réservation: " + (paiement.getReservation() != null ? paiement.getReservation().getIdReservation() : "N/A"));
                    
                    // Vérifier si le paiement peut être supprimé
                    if (paiementService.canDeletePaiement(paiementId)) {
                        try {
                            System.out.println("🔄 Tentative suppression via service...");
                            
                            // Appeler la suppression
                            paiementService.deletePaiement(paiementId);
                            
                            // Vérifier si la suppression a réussi
                            Thread.sleep(500); // Attendre un peu pour la synchronisation
                            Paiement verification = paiementService.getPaiementById(paiementId);
                            
                            if (verification == null) {
                                message = "✅ Paiement ID " + paiementId + " supprimé avec succès !";
                                typeMessage = "success";
                                System.out.println("✅ SUCCÈS: " + message);
                            } else {
                                message = "⚠️ Paiement toujours présent après suppression, essai de suppression forcée...";
                                typeMessage = "warning";
                                System.out.println("⚠️ " + message);
                                
                                // Essai de suppression forcée
                                if (paiementService.forceDeletePaiement(paiementId)) {
                                    message = "✅ Paiement supprimé avec méthode forcée !";
                                    typeMessage = "success";
                                }
                            }
                            
                        } catch (Exception e) {
                            message = "❌ Impossible de supprimer le paiement: " + e.getMessage();
                            typeMessage = "error";
                            System.err.println("❌ ÉCHEC: " + e.getMessage());
                            e.printStackTrace();
                            
                            // Dernière tentative
                            try {
                                if (paiementService.forceDeletePaiement(paiementId)) {
                                    message = "✅ Paiement supprimé avec méthode alternative !";
                                    typeMessage = "success";
                                }
                            } catch (Exception e2) {
                                System.err.println("❌ Échec méthode alternative: " + e2.getMessage());
                            }
                        }
                    } else {
                        message = "❌ Ce paiement ne peut pas être supprimé car il est lié à une réservation";
                        typeMessage = "error";
                        System.out.println("❌ ERREUR: Paiement ne peut pas être supprimé");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ ID invalide: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin suppression paiement ===");
        System.out.println("=".repeat(50) + "\n");
        
        afficherListePaiements(request, response);
    }
    
    // ========== FORMULAIRES D'AFFICHAGE ==========
    
    private void afficherFormulaireAjoutClient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/ajouterClient.jsp").forward(request, response);
    }
    
    private void afficherFormulaireAjoutTerrain(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/ajouterTerrain.jsp").forward(request, response);
    }
    
    private void afficherFormulaireAjoutReservation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Client> clients = clientService.getAllClients();
        List<Terrain> terrains = terrainService.getAllTerrains();
        request.setAttribute("clients", clients);
        request.setAttribute("terrains", terrains);
        request.getRequestDispatcher("/ajouterReservation.jsp").forward(request, response);
    }
    
    private void afficherFormulaireAjoutPaiement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            List<Paiement> paiements = paiementService.getAllPaiements();
            
            System.out.println("📊 Chargement formulaire ajout paiement:");
            System.out.println("   - Réservations totales: " + reservations.size());
            System.out.println("   - Paiements totaux: " + paiements.size());
            
            int reservationsSansPaiement = 0;
            for (Reservation reservation : reservations) {
                boolean aPaiement = false;
                for (Paiement paiement : paiements) {
                    if (paiement.getReservation() != null && 
                        paiement.getReservation().getIdReservation().equals(reservation.getIdReservation())) {
                        aPaiement = true;
                        break;
                    }
                }
                if (!aPaiement) {
                    reservationsSansPaiement++;
                }
            }
            
            System.out.println("   - Réservations sans paiement: " + reservationsSansPaiement);
            
            request.setAttribute("reservations", reservations);
            request.setAttribute("paiements", paiements);
            request.setAttribute("reservationsSansPaiement", reservationsSansPaiement);
            
            request.getRequestDispatcher("/ajouterPaiement.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Erreur lors du chargement des réservations: " + e.getMessage());
            request.setAttribute("typeMessage", "error");
            response.sendRedirect(request.getContextPath() + "/main/reservations");
        }
    }
    
    // ========== TRAITEMENT DES AJOUTS ==========
    
    private void traiterAjoutClient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Client client = new Client();
            client.setNom(request.getParameter("nom"));
            client.setEmail(request.getParameter("email"));
            client.setTelephone(request.getParameter("telephone"));
            
            String dateStr = request.getParameter("dateInscription");
            if (dateStr != null && !dateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                client.setDateInscription(sdf.parse(dateStr));
            } else {
                client.setDateInscription(new Date());
            }
            
            clientService.addClient(client);
            request.setAttribute("message", "✅ Client ajouté avec succès !");
            request.setAttribute("typeMessage", "success");
        } catch (Exception e) {
            request.setAttribute("message", "❌ Erreur : " + e.getMessage());
            request.setAttribute("typeMessage", "error");
        }
        afficherListeClients(request, response);
    }
    
    private void traiterAjoutTerrain(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Terrain terrain = new Terrain();
            terrain.setNom(request.getParameter("nom"));
            terrain.setType(request.getParameter("type"));
            terrain.setLocalisation(request.getParameter("localisation"));
            
            String capaciteStr = request.getParameter("capacite");
            if (capaciteStr != null && !capaciteStr.isEmpty()) {
                terrain.setCapacite(Integer.parseInt(capaciteStr));
            } else {
                terrain.setCapacite(20);
            }
            
            String prixStr = request.getParameter("prixHeure");
            if (prixStr != null && !prixStr.isEmpty()) {
                terrain.setPrixHeure(Double.parseDouble(prixStr));
            } else {
                String type = terrain.getType().toLowerCase();
                switch (type) {
                    case "football": terrain.setPrixHeure(30.0); break;
                    case "tennis": terrain.setPrixHeure(25.0); break;
                    case "basket": terrain.setPrixHeure(20.0); break;
                    case "padel": terrain.setPrixHeure(35.0); break;
                    default: terrain.setPrixHeure(15.0); break;
                }
            }
            
            terrainService.addTerrain(terrain);
            request.setAttribute("message", "✅ Terrain ajouté avec succès !");
            request.setAttribute("typeMessage", "success");
        } catch (Exception e) {
            request.setAttribute("message", "❌ Erreur : " + e.getMessage());
            request.setAttribute("typeMessage", "error");
        }
        afficherListeTerrains(request, response);
    }
    
    private void traiterAjoutReservation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String message = "";
        String typeMessage = "";
        
        try {
            System.out.println("=== SERVLET: Début traitement ajout réservation ===");
            
            String clientIdStr = request.getParameter("clientId");
            String terrainIdStr = request.getParameter("terrainId");
            String dateStr = request.getParameter("dateReservation");
            String heureDebut = request.getParameter("heureDebut");
            String heureFin = request.getParameter("heureFin");
            
            System.out.println("📋 Paramètres reçus:");
            System.out.println("   clientId: " + clientIdStr);
            System.out.println("   terrainId: " + terrainIdStr);
            System.out.println("   date: " + dateStr);
            System.out.println("   heureDebut: " + heureDebut);
            System.out.println("   heureFin: " + heureFin);
            
            if (clientIdStr == null || clientIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Client requis");
            }
            if (terrainIdStr == null || terrainIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Terrain requis");
            }
            if (heureDebut == null || heureDebut.trim().isEmpty()) {
                throw new IllegalArgumentException("Heure de début requise");
            }
            if (heureFin == null || heureFin.trim().isEmpty()) {
                throw new IllegalArgumentException("Heure de fin requise");
            }
            
            Long clientId = Long.parseLong(clientIdStr.trim());
            Long terrainId = Long.parseLong(terrainIdStr.trim());
            
            System.out.println("🔍 Recherche client ID: " + clientId);
            Client client = clientService.getClientById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable avec ID: " + clientId);
            }
            System.out.println("✅ Client trouvé: " + client.getNom());
            
            System.out.println("🔍 Recherche terrain ID: " + terrainId);
            Terrain terrain = terrainService.getTerrainById(terrainId);
            if (terrain == null) {
                throw new IllegalArgumentException("Terrain introuvable avec ID: " + terrainId);
            }
            System.out.println("✅ Terrain trouvé: " + terrain.getNom() + " (" + terrain.getType() + ")");
            
            Reservation reservation = new Reservation();
            
            reservation.setClient(client);
            reservation.setTerrain(terrain);
            
            Date dateReservation;
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateReservation = sdf.parse(dateStr.trim());
            } else {
                dateReservation = new Date();
            }
            reservation.setDateReservation(dateReservation);
            
            reservation.setHeureDebut(heureDebut.trim());
            reservation.setHeureFin(heureFin.trim());
            
            reservation.setModePaiement("non payé");
            System.out.println("💰 Mode paiement défini: non payé");
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date debutTime = sdf.parse(heureDebut.trim());
                Date finTime = sdf.parse(heureFin.trim());
                
                long diffMs = finTime.getTime() - debutTime.getTime();
                long minutes = diffMs / (60 * 1000);
                
                if (minutes <= 0) {
                    throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
                }
                
                reservation.setDuree(minutes);
                System.out.println("⏱️ Durée calculée: " + minutes + " minutes");
                
            } catch (ParseException e) {
                System.err.println("⚠ Format d'heure invalide: " + e.getMessage());
            }
            
            System.out.println("💾 Appel à reservationService.addReservation()...");
            reservationService.addReservation(reservation);
            
            message = "✅ Réservation ajoutée avec succès pour " + client.getNom() + 
                     " sur le terrain " + terrain.getNom() + 
                     " le " + new SimpleDateFormat("dd/MM/yyyy").format(dateReservation) + 
                     " de " + heureDebut + " à " + heureFin;
            typeMessage = "success";
            
            System.out.println("=== SERVLET: Réservation ajoutée avec succès ===");
            
        } catch (NumberFormatException e) {
            message = "❌ Format d'ID invalide. Veuillez sélectionner un client et un terrain valides.";
            typeMessage = "error";
            System.err.println("❌ Erreur de format: " + e.getMessage());
            
        } catch (IllegalArgumentException e) {
            message = "❌ " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            
        } catch (ParseException e) {
            message = "❌ Format de date invalide. Utilisez le format AAAA-MM-JJ.";
            typeMessage = "error";
            System.err.println("❌ Erreur de parsing: " + e.getMessage());
            
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ Erreur système: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        afficherListeReservations(request, response);
    }
    
    private void traiterAjoutPaiement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début traitement ajout paiement ===");
        
        String message = "";
        String typeMessage = "";
        
        try {
            String reservationIdStr = request.getParameter("reservationId");
            String montantStr = request.getParameter("montant");
            String modePaiement = request.getParameter("modePaiement");
            String datePaiementStr = request.getParameter("datePaiement");
            
            System.out.println("📋 Paramètres reçus:");
            System.out.println("   reservationId: " + reservationIdStr);
            System.out.println("   montant: " + montantStr);
            System.out.println("   modePaiement: " + modePaiement);
            System.out.println("   datePaiement: " + datePaiementStr);
            
            if (reservationIdStr == null || reservationIdStr.trim().isEmpty()) {
                message = "❌ Réservation requise";
                typeMessage = "error";
                System.out.println("❌ ERREUR: Réservation manquante");
            } else if (montantStr == null || montantStr.trim().isEmpty()) {
                message = "❌ Montant requis";
                typeMessage = "error";
                System.out.println("❌ ERREUR: Montant manquant");
            } else if (modePaiement == null || modePaiement.trim().isEmpty()) {
                message = "❌ Mode de paiement requis";
                typeMessage = "error";
                System.out.println("❌ ERREUR: Mode paiement manquant");
            } else {
                Long reservationId = Long.parseLong(reservationIdStr.trim());
                Double montant = Double.parseDouble(montantStr.trim());
                
                System.out.println("🔍 Recherche réservation ID: " + reservationId);
                
                Reservation reservation = reservationService.getReservationById(reservationId);
                if (reservation == null) {
                    message = "❌ Réservation introuvable avec ID: " + reservationId;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: Réservation introuvable");
                } else {
                    // Vérifier si la réservation a déjà un paiement
                    System.out.println("🔍 Vérification des paiements existants...");
                    List<Paiement> paiementsReservation = paiementService.getPaiementsParReservation(reservationId);
                    
                    if (!paiementsReservation.isEmpty()) {
                        message = "❌ Cette réservation a déjà un paiement enregistré !";
                        typeMessage = "error";
                        System.out.println("❌ ERREUR: Réservation déjà payée");
                    } else {
                        System.out.println("📋 Réservation trouvée:");
                        System.out.println("   - Client: " + (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A"));
                        System.out.println("   - Terrain: " + (reservation.getTerrain() != null ? reservation.getTerrain().getNom() : "N/A"));
                        System.out.println("   - Statut actuel: " + reservation.getModePaiement());
                        
                        // CRÉATION DU PAIEMENT
                        Paiement paiement = new Paiement();
                        paiement.setMontant(montant);
                        paiement.setModePaiement(modePaiement);
                        
                        // Gestion de la date
                        Date datePaiement;
                        if (datePaiementStr != null && !datePaiementStr.trim().isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            datePaiement = sdf.parse(datePaiementStr.trim());
                        } else {
                            datePaiement = new Date();
                        }
                        paiement.setDatePaiement(datePaiement);
                        
                        // ATTACHER LES RELATIONS (TRÈS IMPORTANT)
                        if (reservation.getClient() != null) {
                            System.out.println("👤 Client attaché au paiement: " + reservation.getClient().getNom());
                            paiement.setClient(reservation.getClient());
                        }
                        
                        paiement.setReservation(reservation);
                        
                        System.out.println("💾 Enregistrement du paiement...");
                        paiementService.addPaiement(paiement);
                        
                        System.out.println("✅ Paiement enregistré avec succès!");
                        System.out.println("🔄 Mise à jour du statut de la réservation...");
                        
                        // MÉTHODE SÉCURISÉE POUR METTRE À JOUR LA RÉSERVATION
                        try {
                            // Récupérer une nouvelle instance managée de la réservation
                            Reservation reservationToUpdate = reservationService.getReservationById(reservationId);
                            if (reservationToUpdate != null) {
                                reservationToUpdate.setModePaiement(modePaiement);
                                reservationToUpdate.setPaiement(paiement);
                                reservationService.updateReservation(reservationToUpdate);
                                System.out.println("✅ Statut de la réservation mis à jour: " + modePaiement);
                            } else {
                                System.out.println("⚠️ Réservation non trouvée pour mise à jour");
                            }
                        } catch (Exception updateException) {
                            System.err.println("⚠️ Erreur lors de la mise à jour de la réservation: " + updateException.getMessage());
                            // Ne pas bloquer le processus pour cette erreur
                        }
                        
                        message = "✅ Paiement enregistré avec succès !<br>" +
                                 "Montant: " + montant + " DH<br>" +
                                 "Mode: " + modePaiement + "<br>" +
                                 "Réservation ID: " + reservationId + " marquée comme payée";
                        typeMessage = "success";
                        
                        System.out.println("✅ SUCCÈS: Paiement enregistré");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ Format de montant invalide. Veuillez entrer un nombre.";
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (ParseException e) {
            message = "❌ Format de date invalide. Utilisez le format AAAA-MM-JJ.";
            typeMessage = "error";
            System.err.println("❌ ERREUR DATE: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin traitement ajout paiement ===");
        System.out.println("=".repeat(50) + "\n");
        
        // Recharger les données avant de réafficher le formulaire
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            request.setAttribute("reservations", reservations);
            request.getRequestDispatcher("/ajouterPaiement.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/main/reservations");
        }
    }
    
    // ========== MÉTHODES DE SUPPRESSION ==========
    
    private void supprimerReservation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début suppression réservation ===");
        
        String message = "";
        String typeMessage = "";
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("ID reçu: " + idStr);
            
            if (idStr == null || idStr.isEmpty()) {
                message = "ID de la réservation non spécifié";
                typeMessage = "error";
                System.out.println("❌ ERREUR: " + message);
            } else {
                Long reservationId = Long.parseLong(idStr);
                System.out.println("ID numérique: " + reservationId);
                
                Reservation reservation = reservationService.getReservationById(reservationId);
                if (reservation == null) {
                    message = "Réservation introuvable avec ID: " + reservationId;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: " + message);
                } else {
                    System.out.println("📋 Réservation à supprimer:");
                    System.out.println("   - Client: " + (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A"));
                    System.out.println("   - Terrain: " + (reservation.getTerrain() != null ? reservation.getTerrain().getNom() : "N/A"));
                    System.out.println("   - Date: " + reservation.getDateReservation());
                    System.out.println("   - Statut paiement: " + reservation.getModePaiement());
                    
                    boolean aPaiement = false;
                    List<Paiement> tousPaiements = paiementService.getAllPaiements();
                    for (Paiement p : tousPaiements) {
                        if (p.getReservation() != null && 
                            p.getReservation().getIdReservation().equals(reservationId)) {
                            aPaiement = true;
                            System.out.println("⚠️ ATTENTION: Réservation a un paiement associé (ID: " + p.getIdPaiement() + ")");
                            break;
                        }
                    }
                    
                    if (aPaiement) {
                        message = "❌ Impossible de supprimer cette réservation car elle a un paiement associé. " +
                                 "Supprimez d'abord le paiement.";
                        typeMessage = "error";
                        System.out.println("❌ ÉCHEC: " + message);
                    } else {
                        try {
                            System.out.println("🔄 Tentative suppression via service...");
                            reservationService.deleteReservation(reservationId);
                            
                            Reservation verification = reservationService.getReservationById(reservationId);
                            if (verification == null) {
                                message = "✅ Réservation ID " + reservationId + " supprimée avec succès !";
                                typeMessage = "success";
                                System.out.println("✅ SUCCÈS: " + message);
                            } else {
                                message = "⚠️ Réservation toujours présente après suppression (vérifiez les logs)";
                                typeMessage = "warning";
                                System.out.println("⚠️ " + message);
                            }
                            
                        } catch (Exception e) {
                            System.err.println("⚠ ERREUR lors de la suppression: " + e.getMessage());
                            e.printStackTrace();
                            
                            message = "❌ Impossible de supprimer la réservation: " + e.getMessage();
                            typeMessage = "error";
                            System.err.println("❌ ÉCHEC: " + message);
                        }
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ ID invalide: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin suppression réservation ===");
        System.out.println("=".repeat(50) + "\n");
        
        afficherListeReservations(request, response);
    }
    
    private void supprimerTerrain(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début suppression terrain ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Méthode: " + request.getMethod());
        System.out.println("Paramètres: " + request.getParameterMap());
        
        String message = "";
        String typeMessage = "";
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("ID reçu: " + idStr);
            
            if (idStr == null || idStr.isEmpty()) {
                message = "ID du terrain non spécifié";
                typeMessage = "error";
                System.out.println("❌ ERREUR: " + message);
            } else {
                Long terrainId = Long.parseLong(idStr);
                System.out.println("ID numérique: " + terrainId);
                
                Terrain terrain = terrainService.getTerrainById(terrainId);
                if (terrain == null) {
                    message = "Terrain introuvable avec ID: " + terrainId;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: " + message);
                } else {
                    System.out.println("📋 Terrain à supprimer: " + terrain.getNom());
                    System.out.println("🏟 Type: " + terrain.getType());
                    System.out.println("📍 Localisation: " + terrain.getLocalisation());
                    
                    try {
                        System.out.println("🔄 Tentative suppression via service...");
                        terrainService.deleteTerrain(terrainId);
                        
                        message = "✅ Terrain '" + terrain.getNom() + "' supprimé avec succès !";
                        typeMessage = "success";
                        System.out.println("✅ SUCCÈS: " + message);
                        
                    } catch (Exception e) {
                        System.err.println("⚠ ERREUR lors de la suppression: " + e.getMessage());
                        e.printStackTrace();
                        
                        message = "❌ Impossible de supprimer le terrain '" + terrain.getNom() + 
                                "'. Vérifiez qu'il n'a pas de réservations en cours.";
                        typeMessage = "error";
                        System.err.println("❌ ÉCHEC: " + message);
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ ID invalide: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin suppression terrain ===");
        System.out.println("=".repeat(50) + "\n");
        
        afficherListeTerrains(request, response);
    }
    
    private void supprimerClient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début suppression client ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Méthode: " + request.getMethod());
        System.out.println("Paramètres: " + request.getParameterMap());
        
        String message = "";
        String typeMessage = "";
        
        try {
            String idStr = request.getParameter("id");
            System.out.println("ID reçu: " + idStr);
            
            if (idStr == null || idStr.isEmpty()) {
                message = "ID du client non spécifié";
                typeMessage = "error";
                System.out.println("❌ ERREUR: " + message);
            } else {
                Long clientId = Long.parseLong(idStr);
                System.out.println("ID numérique: " + clientId);
                
                Client client = clientService.getClientById(clientId);
                if (client == null) {
                    message = "Client introuvable avec ID: " + clientId;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: " + message);
                } else {
                    System.out.println("📋 Client à supprimer: " + client.getNom());
                    System.out.println("📧 Email: " + client.getEmail());
                    
                    System.out.println("🔄 Tentative suppression méthode standard...");
                    boolean success = clientService.deleteClient(clientId);
                    
                    if (!success) {
                        System.out.println("🔄 Méthode standard échouée, tentative méthode avancée...");
                        success = clientService.deleteClientWithConstraints(clientId);
                    }
                    
                    if (success) {
                        message = "✅ Client '" + client.getNom() + "' supprimé avec succès !";
                        typeMessage = "success";
                        System.out.println("✅ SUCCÈS: " + message);
                    } else {
                        message = "❌ Impossible de supprimer le client. Vérifiez qu'il n'a pas de réservations ou paiements en cours.";
                        typeMessage = "error";
                        System.out.println("❌ ÉCHEC: " + message);
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ ID invalide: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin suppression client ===");
        System.out.println("=".repeat(50) + "\n");
        
        afficherListeClients(request, response);
    }
    
    // ========== FONCTIONNALITÉS DEMANDÉES ==========
    
    private void afficherClientsFootball(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Object[]> clients = clientService.getClientsFootballTries();
            request.setAttribute("clientsFootball", clients);
            request.getRequestDispatcher("/clientsFootball.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur : " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private void afficherModifierPaiement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== CHARGEMENT PAGE MODIFICATION PAIEMENT ===");
        
        try {
            // Synchroniser d'abord
            System.out.println("🔄 Synchronisation préalable...");
            reservationService.synchroniserToutesReservationsAvecPaiements();
            
            // Récupérer les réservations payées via le service
            System.out.println("🔍 Recherche réservations payées via service...");
            List<Reservation> reservationsPayees = reservationService.getReservationsPayees();
            
            // Si la méthode n'existe pas, utiliser une alternative
            if (reservationsPayees == null) {
                System.out.println("⚠️ Méthode getReservationsPayees() non disponible, utilisation alternative...");
                reservationsPayees = filtrerReservationsPayees();
            }
            
            System.out.println("📊 Réservations avec paiements: " + reservationsPayees.size());
            
            // Log détaillé
            for (Reservation r : reservationsPayees) {
                System.out.println("💰 Réservation ID: " + r.getIdReservation() + 
                                 " | Mode: " + r.getModePaiement() +
                                 " | Client: " + (r.getClient() != null ? r.getClient().getNom() : "N/A") +
                                 " | Terrain: " + (r.getTerrain() != null ? r.getTerrain().getNom() : "N/A"));
            }
            
            request.setAttribute("reservationsPayees", reservationsPayees);
            request.getRequestDispatcher("/modifierPaiement.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement page modification: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("message", "Erreur: " + e.getMessage());
            request.setAttribute("typeMessage", "error");
            request.getRequestDispatcher("/modifierPaiement.jsp").forward(request, response);
        }
    }
    
    // Méthode alternative pour filtrer les réservations payées
    private List<Reservation> filtrerReservationsPayees() {
        List<Reservation> resultat = new ArrayList<>();
        
        try {
            // Récupérer toutes les réservations
            List<Reservation> toutesReservations = reservationService.getAllReservations();
            
            // Filtrer celles qui sont payées (modePaiement différent de "non payé")
            for (Reservation reservation : toutesReservations) {
                String modePaiement = reservation.getModePaiement();
                if (modePaiement != null && !"non payé".equals(modePaiement.trim())) {
                    resultat.add(reservation);
                }
            }
            
            // Trier par date décroissante
            resultat.sort((r1, r2) -> {
                if (r1.getDateReservation() == null && r2.getDateReservation() == null) return 0;
                if (r1.getDateReservation() == null) return 1;
                if (r2.getDateReservation() == null) return -1;
                return r2.getDateReservation().compareTo(r1.getDateReservation());
            });
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du filtrage des réservations payées: " + e.getMessage());
        }
        
        return resultat;
    }
    
    // ========== MÉTHODE MODIFIER PAIEMENT - CORRIGÉE ==========
    
    private void modifierPaiement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Début modification paiement ===");
        
        String message = "";
        String typeMessage = "";
        
        try {
            String idStr = request.getParameter("idReservation");
            String modePaiement = request.getParameter("modePaiement");
            
            System.out.println("📋 Paramètres reçus:");
            System.out.println("   idReservation: " + idStr);
            System.out.println("   modePaiement: " + modePaiement);
            
            if (idStr == null || idStr.trim().isEmpty()) {
                message = "❌ ID de réservation requis";
                typeMessage = "error";
                System.out.println("❌ ERREUR: ID manquant");
            } else if (modePaiement == null || modePaiement.trim().isEmpty()) {
                message = "❌ Mode de paiement requis";
                typeMessage = "error";
                System.out.println("❌ ERREUR: Mode paiement manquant");
            } else {
                Long idReservation = Long.parseLong(idStr.trim());
                
                System.out.println("🔍 Recherche réservation ID: " + idReservation);
                
                Reservation reservation = reservationService.getReservationById(idReservation);
                
                if (reservation == null) {
                    message = "❌ Réservation introuvable avec ID: " + idReservation;
                    typeMessage = "error";
                    System.out.println("❌ ERREUR: Réservation introuvable");
                } else {
                    System.out.println("📋 Réservation trouvée:");
                    System.out.println("   - ID: " + reservation.getIdReservation());
                    System.out.println("   - Client: " + (reservation.getClient() != null ? reservation.getClient().getNom() : "N/A"));
                    System.out.println("   - Terrain: " + (reservation.getTerrain() != null ? reservation.getTerrain().getNom() : "N/A"));
                    System.out.println("   - Mode actuel: " + reservation.getModePaiement());
                    
                    String ancienMode = reservation.getModePaiement();
                    
                    // Vérifier que la réservation est payée
                    if ("non payé".equals(ancienMode) || ancienMode == null) {
                        message = "❌ Cette réservation n'est pas encore payée. Veuillez d'abord ajouter un paiement.";
                        typeMessage = "error";
                        System.out.println("❌ ERREUR: Réservation non payée");
                    } 
                    // Vérifier que le nouveau mode est différent de "non payé"
                    else if ("non payé".equals(modePaiement)) {
                        message = "❌ Vous ne pouvez pas définir le mode de paiement sur 'non payé' via cette page";
                        typeMessage = "error";
                        System.out.println("❌ ERREUR: Mode invalide");
                    } 
                    // Vérifier si le mode est différent
                    else if (modePaiement.equals(ancienMode)) {
                        message = "⚠️ Le mode de paiement est déjà '" + modePaiement + "'. Aucune modification nécessaire.";
                        typeMessage = "info";
                        System.out.println("⚠️ Mode identique, pas de modification");
                    } 
                    else {
                        System.out.println("🔄 Mise à jour du mode de paiement de la réservation...");
                        
                        // SIMPLE MISE À JOUR DU MODE DE PAIEMENT DE LA RÉSERVATION
                        reservation.setModePaiement(modePaiement);
                        reservationService.updateReservation(reservation);
                        System.out.println("✅ Réservation mise à jour: " + ancienMode + " → " + modePaiement);
                        
                        // OPTIONNEL : Si vous voulez aussi mettre à jour le paiement associé
                        try {
                            // Vérifier s'il y a un paiement associé à cette réservation
                            List<Paiement> paiementsReservation = paiementService.getPaiementsParReservation(idReservation);
                            
                            if (!paiementsReservation.isEmpty()) {
                                System.out.println("💰 " + paiementsReservation.size() + " paiement(s) trouvé(s) pour cette réservation");
                                
                                // Mettre à jour tous les paiements associés (normalement un seul)
                                for (Paiement paiement : paiementsReservation) {
                                    try {
                                        System.out.println("🔄 Mise à jour du paiement associé ID: " + paiement.getIdPaiement());
                                        System.out.println("   Ancien mode: " + paiement.getModePaiement() + " → Nouveau: " + modePaiement);
                                        
                                        paiement.setModePaiement(modePaiement);
                                        paiementService.updatePaiement(paiement);
                                        
                                        System.out.println("✅ Paiement associé mis à jour");
                                    } catch (Exception e) {
                                        System.err.println("⚠️ Erreur lors de la mise à jour du paiement " + paiement.getIdPaiement() + 
                                                         ": " + e.getMessage());
                                        // Ne pas bloquer le processus pour cette erreur
                                    }
                                }
                            } else {
                                System.out.println("ℹ️ Aucun paiement associé trouvé pour cette réservation");
                            }
                        } catch (Exception e) {
                            System.err.println("⚠️ Erreur lors de la recherche des paiements associés: " + e.getMessage());
                            // Ne pas bloquer le processus principal
                        }
                        
                        message = "✅ Mode de paiement modifié avec succès !<br>" +
                                 "Réservation ID: " + idReservation + "<br>" +
                                 "Ancien mode: " + ancienMode + " → Nouveau mode: " + modePaiement;
                        typeMessage = "success";
                        
                        // Vérifier la mise à jour
                        System.out.println("🔍 Vérification de la mise à jour...");
                        Reservation updatedReservation = reservationService.getReservationById(idReservation);
                        if (updatedReservation != null && modePaiement.equals(updatedReservation.getModePaiement())) {
                            System.out.println("✅ Vérification réussie: Réservation mise à jour en base");
                        } else {
                            System.out.println("⚠️ Problème: Réservation non mise à jour en base");
                        }
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ Format d'ID invalide. Veuillez entrer un nombre.";
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Recharger les réservations payées pour l'affichage
        System.out.println("🔄 Rechargement des réservations payées pour l'affichage...");
        List<Reservation> reservationsPayees = filtrerReservationsPayees();
        request.setAttribute("reservationsPayees", reservationsPayees);
        System.out.println("📊 Réservations payées chargées: " + reservationsPayees.size());
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin modification paiement ===");
        System.out.println("=".repeat(50) + "\n");
        
        request.getRequestDispatcher("/modifierPaiement.jsp").forward(request, response);
    }
    private void afficherReservationsMars2025(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Reservation> reservations = reservationService.getReservationsMars2025();
            request.setAttribute("reservations", reservations);
            request.getRequestDispatcher("/reservationsMars2025.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur : " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private void afficherReservationsParTerrain(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Object[]> stats = terrainService.getReservationsParTerrain();
            request.setAttribute("statsTerrains", stats);
            request.getRequestDispatcher("/reservationsParTerrain.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur : " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private void afficherTerrainsNonReserves(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Object[]> terrains = terrainService.getTerrainsNonReserves();
            request.setAttribute("terrainsNonReserves", terrains);
            request.getRequestDispatcher("/terrainsNonReserves.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur : " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private void afficherMontantTotalParClient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Object[]> montants = clientService.getMontantTotalParClient();
            request.setAttribute("montantsClients", montants);
            
            double totalAmount = 0.0;
            for (Object[] clientData : montants) {
                if (clientData.length > 2 && clientData[2] != null) {
                    totalAmount += ((Number) clientData[2]).doubleValue();
                }
            }
            request.setAttribute("totalAmount", totalAmount);
            
            request.getRequestDispatcher("/montantTotalParClient.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur : " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private void afficherSupprimerReservationsAvant(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/supprimerReservations.jsp").forward(request, response);
    }
    
    private void executerSupprimerReservationsAvant(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("🔥 DÉBUT SUPPRESSION RÉSERVATIONS PAR ANNÉE");
        
        String message = "";
        String typeMessage = "";
        
        try {
            String anneeStr = request.getParameter("annee");
            System.out.println("📅 Année reçue: " + anneeStr);
            
            if (anneeStr == null || anneeStr.trim().isEmpty()) {
                message = "❌ Erreur: Veuillez spécifier une année";
                typeMessage = "error";
            } else {
                int annee = Integer.parseInt(anneeStr.trim());
                
                if (annee < 2000 || annee > 2025) {
                    message = "❌ Erreur: L'année doit être entre 2000 et 2025";
                    typeMessage = "error";
                } else {
                    System.out.println("🗑️ Suppression des réservations avant l'année: " + annee);
                    
                    Calendar cal = Calendar.getInstance();
                    cal.set(annee, Calendar.JANUARY, 1, 0, 0, 0);
                    Date dateLimite = cal.getTime();
                    
                    System.out.println("📅 Date limite: " + dateLimite);
                    
                    Long countBefore = countReservationsAvantDate(dateLimite);
                    System.out.println("📊 Réservations à supprimer: " + countBefore);
                    
                    if (countBefore > 0) {
                        reservationService.deleteReservationsAvantDate(dateLimite);
                        
                        message = "✅ " + countBefore + " réservation(s) antérieure(s) à " + annee + " supprimée(s) avec succès !";
                        typeMessage = "success";
                        System.out.println("✅ SUPPRESSION RÉUSSIE");
                    } else {
                        message = "ℹ️ Aucune réservation trouvée antérieure à " + annee;
                        typeMessage = "info";
                        System.out.println("ℹ️ AUCUNE RÉSERVATION À SUPPRIMER");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            message = "❌ Erreur: L'année doit être un nombre valide";
            typeMessage = "error";
            System.err.println("❌ ERREUR FORMAT: " + e.getMessage());
        } catch (Exception e) {
            message = "❌ Erreur système: " + e.getMessage();
            typeMessage = "error";
            System.err.println("❌ ERREUR SYSTÈME: " + e.getMessage());
            e.printStackTrace();
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("🔥 FIN SUPPRESSION RÉSERVATIONS PAR ANNÉE\n");
        
        afficherSupprimerReservationsAvant(request, response);
    }
    
    private Long countReservationsAvantDate(Date date) {
        try {
            List<Reservation> allReservations = reservationService.getAllReservations();
            
            long count = 0;
            
            for (Reservation r : allReservations) {
                if (r.getDateReservation() != null && r.getDateReservation().before(date)) {
                    count++;
                }
            }
            
            return count;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur countReservationsAvantDate: " + e.getMessage());
            return 0L;
        }
    }
    
    // ========== MÉTHODES D'AFFICHAGE DES LISTES ==========
    
    private void afficherListeClients(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Client> clients = clientService.getAllClients();
        request.setAttribute("clients", clients);
        request.getRequestDispatcher("/listeClients.jsp").forward(request, response);
    }
    
    private void afficherListeTerrains(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Terrain> terrains = terrainService.getAllTerrains();
        request.setAttribute("terrains", terrains);
        request.getRequestDispatcher("/listeTerrains.jsp").forward(request, response);
    }
    
    private void afficherListeReservations(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            List<Paiement> paiements = paiementService.getAllPaiements();
            
            for (Reservation reservation : reservations) {
                boolean aPaiement = false;
                for (Paiement paiement : paiements) {
                    if (paiement.getReservation() != null && 
                        paiement.getReservation().getIdReservation().equals(reservation.getIdReservation())) {
                        aPaiement = true;
                        reservation.setPaiement(paiement);
                        break;
                    }
                }
                if (!aPaiement) {
                    reservation.setPaiement(null);
                }
            }
            
            request.setAttribute("reservations", reservations);
            request.getRequestDispatcher("/listeReservations.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Erreur lors du chargement des réservations: " + e.getMessage());
            request.setAttribute("typeMessage", "error");
            response.sendRedirect(request.getContextPath() + "/main/dashboard");
        }
    }
    
    private void afficherListePaiements(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Paiement> paiements = paiementService.getAllPaiements();
        request.setAttribute("paiements", paiements);
        request.getRequestDispatcher("/listePaiements.jsp").forward(request, response);
    }
    
    // ========== DASHBOARD ET TEST ==========
    
    private void afficherDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Client> clients = clientService.getAllClients();
        List<Terrain> terrains = terrainService.getAllTerrains();
        List<Reservation> reservations = reservationService.getAllReservations();
        List<Paiement> paiements = paiementService.getAllPaiements();
        
        request.setAttribute("nombreClients", clients.size());
        request.setAttribute("nombreTerrains", terrains.size());
        request.setAttribute("nombreReservations", reservations.size());
        request.setAttribute("nombrePaiements", paiements.size());
        
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
    
    private void testerSuppressionReservation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== SERVLET: Test DEBUG suppression réservation ===");
        
        String idStr = request.getParameter("id");
        String message = "";
        String typeMessage = "";
        
        if (idStr == null || idStr.isEmpty()) {
            message = "ID de réservation non spécifié";
            typeMessage = "error";
        } else {
            try {
                Long id = Long.parseLong(idStr);
                System.out.println("🧪 Test suppression réservation ID: " + id);
                
                Reservation reservation = reservationService.getReservationById(id);
                if (reservation == null) {
                    message = "❌ Réservation introuvable avec ID: " + id;
                    typeMessage = "error";
                } else {
                    System.out.println("📋 Réservation trouvée:");
                    System.out.println("   - Client: " + reservation.getClient().getNom());
                    System.out.println("   - Terrain: " + reservation.getTerrain().getNom());
                    
                    System.out.println("🔄 Appel à reservationService.deleteReservation()...");
                    reservationService.deleteReservation(id);
                    
                    Reservation verification = reservationService.getReservationById(id);
                    if (verification == null) {
                        message = "✅ Réservation supprimée avec succès !";
                        typeMessage = "success";
                        System.out.println("✅ SUCCÈS: Réservation supprimée");
                    } else {
                        message = "⚠️ Réservation toujours présente après suppression";
                        typeMessage = "warning";
                        System.out.println("⚠️ ÉCHEC: Réservation toujours présente");
                    }
                }
                
            } catch (Exception e) {
                message = "❌ Erreur: " + e.getMessage();
                typeMessage = "error";
                System.err.println("❌ ERREUR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        request.setAttribute("message", message);
        request.setAttribute("typeMessage", typeMessage);
        
        System.out.println("=== SERVLET: Fin test DEBUG ===");
        System.out.println("=".repeat(50) + "\n");
        
        afficherListeReservations(request, response);
    }
}