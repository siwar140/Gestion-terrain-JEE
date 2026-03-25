<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter un Paiement</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --lilas: #8a4baf;
            --lilas-clair: #d4b3e8;
            --lilas-fonce: #6b3a8e;
        }
        
        body { 
            background-color: #f8f9fa;
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 600px;
            margin-top: 40px;
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(138, 75, 175, 0.1);
            border: 1px solid var(--lilas-clair);
        }
        
        .form-header {
            background-color: var(--lilas);
            color: white;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 25px;
            text-align: center;
        }
        
        .btn-submit {
            background-color: var(--lilas);
            color: white;
            border: none;
            padding: 10px 25px;
            font-weight: 600;
            border-radius: 6px;
            transition: background-color 0.3s;
        }
        
        .btn-submit:hover {
            background-color: var(--lilas-fonce);
        }
        
        .btn-secondary {
            background-color: #f0f0f0;
            color: var(--lilas-fonce);
            border: 1px solid var(--lilas-clair);
        }
        
        .montant-auto {
            font-size: 0.9em;
            color: #28a745;
            margin-top: 5px;
        }
        
        .reservation-option {
            padding: 8px 12px;
            border-left: 4px solid transparent;
        }
        
        .reservation-option:hover {
            background-color: #f8f9fa;
        }
        
        .reservation-id {
            display: inline-block;
            width: 70px;
            font-weight: bold;
            color: var(--lilas-fonce);
        }
        
        .reservation-client {
            display: inline-block;
            width: 180px;
            color: #555;
        }
        
        .reservation-terrain {
            display: inline-block;
            width: 150px;
            color: #777;
        }
        
        .non-payee-badge {
            background-color: #ffc107;
            color: #212529;
            font-size: 0.7em;
            padding: 2px 6px;
            border-radius: 10px;
            margin-left: 8px;
            font-weight: bold;
        }
        
        .no-reservations {
            text-align: center;
            padding: 30px;
            color: #666;
        }
        
        .no-reservations i {
            font-size: 2.5em;
            margin-bottom: 15px;
            color: #ddd;
        }
        
        .info-box {
            background-color: rgba(138, 75, 175, 0.1);
            border: 1px solid var(--lilas-clair);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .info-box i {
            color: var(--lilas);
            margin-right: 10px;
        }
        
        .selected-reservation {
            background-color: rgba(138, 75, 175, 0.1);
            border: 2px solid var(--lilas-clair);
            border-radius: 8px;
            padding: 15px;
            margin-top: 15px;
            margin-bottom: 20px;
        }
        
        .reservation-detail {
            margin-bottom: 5px;
        }
        
        .reservation-label {
            font-weight: 600;
            color: var(--lilas-fonce);
            width: 120px;
            display: inline-block;
        }
        
        .message-alert {
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .message-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .message-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .message-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        
        .message-warning {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-0">
                <i class="fas fa-credit-card me-2"></i>Nouveau Paiement
            </h5>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/paiements" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Retour aux paiements
            </a>
            <a href="${pageContext.request.contextPath}/main/reservations" class="btn btn-secondary btn-sm ms-2">
                <i class="fas fa-calendar-alt me-1"></i> Voir les réservations
            </a>
        </div>

        <!-- Affichage des messages -->
        <c:if test="${not empty message}">
            <div class="message-alert message-${typeMessage}">
                <i class="fas 
                    <c:choose>
                        <c:when test="${typeMessage == 'success'}">fa-check-circle</c:when>
                        <c:when test="${typeMessage == 'error'}">fa-exclamation-circle</c:when>
                        <c:when test="${typeMessage == 'warning'}">fa-exclamation-triangle</c:when>
                        <c:otherwise>fa-info-circle</c:otherwise>
                    </c:choose>
                me-2"></i>
                ${message}
            </div>
        </c:if>

        <!-- Vérifier si des réservations non payées existent -->
        <c:set var="hasNonPayees" value="false" />
        <c:set var="nonPayeesCount" value="0" />
        
        <!-- COMPTER LES RÉSERVATIONS NON PAYÉES -->
        <c:forEach var="reservation" items="${reservations}">
            <c:choose>
                <c:when test="${reservation.modePaiement == null}">
                    <c:set var="hasNonPayees" value="true" />
                    <c:set var="nonPayeesCount" value="${nonPayeesCount + 1}" />
                </c:when>
                <c:when test="${reservation.modePaiement == 'non payé'}">
                    <c:set var="hasNonPayees" value="true" />
                    <c:set var="nonPayeesCount" value="${nonPayeesCount + 1}" />
                </c:when>
                <c:when test="${empty reservation.modePaiement}">
                    <c:set var="hasNonPayees" value="true" />
                    <c:set var="nonPayeesCount" value="${nonPayeesCount + 1}" />
                </c:when>
            </c:choose>
        </c:forEach>

        <c:choose>
            <c:when test="${hasNonPayees}">
                <!-- Info box -->
                <div class="info-box">
                    <i class="fas fa-info-circle"></i>
                    <strong>${nonPayeesCount} réservation(s) non payée(s)</strong> disponible(s) pour enregistrer un paiement.
                </div>

                <!-- Formulaire -->
                <form action="${pageContext.request.contextPath}/main/ajouterPaiement" method="POST" id="paiementForm">
                    
                    <div class="mb-4">
                        <label for="reservationId" class="form-label">
                            <i class="fas fa-calendar-check me-1"></i>Réservation <span class="text-danger">*</span>
                        </label>
                        <select class="form-select" id="reservationId" name="reservationId" required>
                            <option value="">-- Sélectionnez une réservation non payée --</option>
                            <c:forEach var="reservation" items="${reservations}">
                                <c:choose>
                                    <c:when test="${reservation.modePaiement == null or reservation.modePaiement == 'non payé' or empty reservation.modePaiement}">
                                        <option value="${reservation.idReservation}" 
                                                data-duree="${reservation.duree}"
                                                data-prix="${reservation.terrain.prixHeure}"
                                                data-client="${reservation.client.nom}"
                                                data-terrain="${reservation.terrain.nom}">
                                            ID: ${reservation.idReservation} | 
                                            Client: ${reservation.client.nom} | 
                                            Terrain: ${reservation.terrain.nom} 
                                            <span class="badge bg-warning text-dark ms-2">NON PAYÉ</span>
                                        </option>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                        </select>
                        <div class="form-text mt-2">
                            <i class="fas fa-filter me-1"></i>
                            Sélectionnez une réservation pour calculer automatiquement le montant.
                        </div>
                    </div>

                    <!-- Détails de la réservation sélectionnée -->
                    <div id="reservationDetails" class="selected-reservation" style="display: none;">
                        <h6 class="mb-3" style="color: var(--lilas-fonce);">
                            <i class="fas fa-info-circle me-2"></i>Détails de la réservation sélectionnée
                        </h6>
                        <div class="reservation-detail">
                            <span class="reservation-label">Client:</span>
                            <span id="detailClient">-</span>
                        </div>
                        <div class="reservation-detail">
                            <span class="reservation-label">Terrain:</span>
                            <span id="detailTerrain">-</span>
                        </div>
                        <div class="reservation-detail">
                            <span class="reservation-label">Durée:</span>
                            <span id="detailDuree">-</span>
                        </div>
                        <div class="reservation-detail">
                            <span class="reservation-label">Prix/heure:</span>
                            <span id="detailPrix">-</span>
                        </div>
                        <div class="reservation-detail">
                            <span class="reservation-label">Statut:</span>
                            <span class="badge bg-warning text-dark">NON PAYÉ</span>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="montant" class="form-label">
                            <i class="fas fa-money-bill-wave me-1"></i>Montant <span class="text-danger">*</span>
                        </label>
                        <div class="input-group">
                            <input type="number" class="form-control" id="montant" 
                                   name="montant" required
                                   min="0" step="0.01" placeholder="0.00">
                            <span class="input-group-text">DH</span>
                        </div>
                        <div class="montant-auto" id="montantInfo" style="display: none;">
                            <i class="fas fa-calculator"></i> 
                            <span id="montantMessage">Montant suggéré basé sur la durée et le tarif du terrain</span>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="modePaiement" class="form-label">
                            <i class="fas fa-credit-card me-1"></i>Mode de paiement <span class="text-danger">*</span>
                        </label>
                        <select class="form-select" id="modePaiement" name="modePaiement" required>
                            <option value="">-- Sélectionnez un mode de paiement --</option>
                            <option value="carte bancaire">Carte bancaire</option>
                            <option value="virement">Virement bancaire</option>
                           
                        </select>
                    </div>

                    <div class="mb-4">
                        <label for="datePaiement" class="form-label">
                            <i class="fas fa-calendar-day me-1"></i>Date du paiement
                        </label>
                        <input type="date" class="form-control" id="datePaiement" 
                               name="datePaiement">
                    </div>

                    <!-- Boutons -->
                    <div class="d-flex justify-content-between mt-4 pt-3 border-top">
                        <button type="reset" class="btn btn-secondary" onclick="resetForm()">
                            <i class="fas fa-redo me-1"></i>Réinitialiser
                        </button>
                        <button type="submit" class="btn btn-submit">
                            <i class="fas fa-save me-1"></i>Enregistrer le paiement
                        </button>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <!-- Message si aucune réservation non payée -->
                <div class="no-reservations">
                    <i class="fas fa-check-circle"></i>
                    <h4 class="mb-3">Toutes les réservations sont déjà payées !</h4>
                    <p class="mb-4">Aucune réservation non payée n'est disponible pour enregistrer un nouveau paiement.</p>
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/main/ajouterReservation" class="btn btn-primary">
                            <i class="fas fa-plus-circle me-2"></i>Créer une nouvelle réservation
                        </a>
                        <a href="${pageContext.request.contextPath}/main/modifierPaiement" class="btn btn-outline-secondary">
                            <i class="fas fa-edit me-2"></i>Modifier un paiement existant
                        </a>
                        <a href="${pageContext.request.contextPath}/main/paiements" class="btn btn-outline-primary">
                            <i class="fas fa-list me-2"></i>Voir tous les paiements
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Collecter les données des réservations non payées
        const reservationsNonPayees = {};
        let nonPayeesCountJS = 0;
        
        // Récupérer les données des réservations depuis les options du select
        document.addEventListener('DOMContentLoaded', function() {
            const reservationSelect = document.getElementById('reservationId');
            if (reservationSelect) {
                Array.from(reservationSelect.options).forEach(option => {
                    if (option.value) {
                        const duree = option.getAttribute('data-duree');
                        const prix = option.getAttribute('data-prix');
                        const client = option.getAttribute('data-client');
                        const terrain = option.getAttribute('data-terrain');
                        
                        if (duree && prix && client && terrain) {
                            reservationsNonPayees[option.value] = {
                                client: client,
                                terrain: terrain,
                                duree: parseFloat(duree) || 60, // Valeur par défaut 60 minutes
                                prixHeure: parseFloat(prix) || 30.0 // Valeur par défaut 30 DH
                            };
                            nonPayeesCountJS++;
                        }
                    }
                });
            }
            
            console.log("Réservations non payées disponibles:", nonPayeesCountJS);
            console.log("Détails:", reservationsNonPayees);
            
            // Date du jour par défaut
            const today = new Date().toISOString().split('T')[0];
            const dateInput = document.getElementById('datePaiement');
            if (dateInput) {
                dateInput.value = today;
            }
            
            // Calcul du montant quand une réservation est sélectionnée
            if (reservationSelect) {
                reservationSelect.addEventListener('change', function() {
                    const reservationId = this.value;
                    const montantInput = document.getElementById('montant');
                    const montantInfo = document.getElementById('montantInfo');
                    const detailsDiv = document.getElementById('reservationDetails');
                    
                    console.log("Réservation sélectionnée:", reservationId);
                    console.log("Données disponibles:", reservationsNonPayees[reservationId]);
                    
                    if (reservationId && reservationsNonPayees[reservationId]) {
                        const reservation = reservationsNonPayees[reservationId];
                        
                        // Afficher les détails
                        document.getElementById('detailClient').textContent = reservation.client;
                        document.getElementById('detailTerrain').textContent = reservation.terrain;
                        
                        // Formater la durée
                        const heures = Math.floor(reservation.duree / 60);
                        const minutes = reservation.duree % 60;
                        let dureeText = heures + 'h';
                        if (minutes > 0) {
                            dureeText += minutes + 'min';
                        }
                        document.getElementById('detailDuree').textContent = dureeText;
                        
                        document.getElementById('detailPrix').textContent = reservation.prixHeure + ' DH/heure';
                        detailsDiv.style.display = 'block';
                        
                        // Calcul du montant : (durée en heures) × (prix par heure)
                        const dureeHeures = reservation.duree / 60;
                        const montantCalculé = dureeHeures * reservation.prixHeure;
                        
                        // Formater pour afficher 2 décimales
                        montantInput.value = montantCalculé.toFixed(2);
                        
                        // Afficher l'info
                        document.getElementById('montantMessage').textContent = 
                            'Calcul automatique : ' + dureeText + ' × ' + reservation.prixHeure + ' DH/h = ' + 
                            montantCalculé.toFixed(2) + ' DH';
                        montantInfo.style.display = 'block';
                    } else {
                        if (montantInput) montantInput.value = '';
                        if (montantInfo) montantInfo.style.display = 'none';
                        if (detailsDiv) detailsDiv.style.display = 'none';
                    }
                });
            }
            
            // Si un message d'erreur est affiché, on fait défiler vers le haut
            const messageAlert = document.querySelector('.message-alert');
            if (messageAlert) {
                window.scrollTo(0, 0);
            }
        });

        function resetForm() {
            const form = document.getElementById('paiementForm');
            if (form) {
                form.reset();
                const dateInput = document.getElementById('datePaiement');
                if (dateInput) {
                    const today = new Date().toISOString().split('T')[0];
                    dateInput.value = today;
                }
                const montantInfo = document.getElementById('montantInfo');
                if (montantInfo) {
                    montantInfo.style.display = 'none';
                }
                const detailsDiv = document.getElementById('reservationDetails');
                if (detailsDiv) {
                    detailsDiv.style.display = 'none';
                }
            }
        }
        
        // Validation du formulaire
        const form = document.getElementById('paiementForm');
        if (form) {
            form.addEventListener('submit', function(e) {
                const reservationId = document.getElementById('reservationId').value;
                const montant = document.getElementById('montant').value;
                const modePaiement = document.getElementById('modePaiement').value;
                
                if (!reservationId) {
                    alert('Veuillez sélectionner une réservation non payée');
                    e.preventDefault();
                    return;
                }
                
                if (!reservationsNonPayees[reservationId]) {
                    alert('Cette réservation n\'est pas valide ou est déjà payée');
                    e.preventDefault();
                    return;
                }
                
                if (!montant || parseFloat(montant) <= 0) {
                    alert('Veuillez saisir un montant valide (supérieur à 0)');
                    e.preventDefault();
                    return;
                }
                
                if (!modePaiement) {
                    alert('Veuillez sélectionner un mode de paiement');
                    e.preventDefault();
                    return;
                }
                
                // Confirmation
                const reservation = reservationsNonPayees[reservationId];
                if (reservation) {
                    if (!confirm(`Confirmer l'enregistrement du paiement ?\n\n👤 Client: ${reservation.client}\n🏟️ Terrain: ${reservation.terrain}\n💰 Montant: ${montant} DH\n💳 Mode: ${modePaiement}`)) {
                        e.preventDefault();
                    }
                }
            });
        }
    </script>
</body>
</html>