<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifier Mode de Paiement</title>
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
            max-width: 800px;
            margin-top: 20px;
        }
        
        .form-container {
            background: white;
            border-radius: 10px;
            padding: 30px;
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
        
        .form-header h4 {
            margin: 0;
            font-weight: 500;
        }
        
        .btn-primary {
            background-color: var(--lilas);
            border-color: var(--lilas);
        }
        
        .btn-primary:hover {
            background-color: var(--lilas-fonce);
            border-color: var(--lilas-fonce);
        }
        
        .btn-outline-secondary {
            color: var(--lilas-fonce);
            border-color: var(--lilas-clair);
        }
        
        .btn-outline-secondary:hover {
            background-color: var(--lilas-clair);
            border-color: var(--lilas);
            color: var(--lilas-fonce);
        }
        
        .mode-option {
            padding: 15px;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .mode-option:hover {
            background-color: #f8f9fa;
            border-color: var(--lilas-clair);
        }
        
        .mode-option.selected {
            border-color: var(--lilas);
            background-color: rgba(138, 75, 175, 0.1);
        }
        
        .form-check-input:checked {
            background-color: var(--lilas);
            border-color: var(--lilas);
        }
        
        .alert-info {
            background-color: rgba(138, 75, 175, 0.1);
            border-color: var(--lilas-clair);
            color: var(--lilas-fonce);
        }
        
        .form-label {
            color: var(--lilas-fonce);
            font-weight: 600;
        }
        
        .form-control:focus {
            border-color: var(--lilas);
            box-shadow: 0 0 0 0.25rem rgba(138, 75, 175, 0.25);
        }
        
        .nav-links {
            margin-bottom: 20px;
        }
        
        .nav-links .btn {
            background-color: #f0f0f0;
            color: var(--lilas-fonce);
            border: 1px solid var(--lilas-clair);
        }
        
        .mode-icon {
            font-size: 1.2em;
            margin-right: 10px;
            width: 24px;
            text-align: center;
        }
        
        /* Style pour les options de la liste déroulante */
        .reservation-option {
            padding: 8px 12px;
        }
        
        .reservation-id {
            display: inline-block;
            width: 60px;
            font-weight: bold;
            color: var(--lilas-fonce);
        }
        
        .reservation-client {
            display: inline-block;
            width: 200px;
        }
        
        .reservation-terrain {
            display: inline-block;
            width: 150px;
        }
        
        /* Style pour l'affichage de la réservation sélectionnée */
        .selected-reservation {
            background-color: rgba(138, 75, 175, 0.1);
            border: 2px solid var(--lilas-clair);
            border-radius: 8px;
            padding: 15px;
            margin-top: 15px;
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
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .no-data i {
            font-size: 3em;
            margin-bottom: 20px;
            color: var(--lilas-clair);
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Navigation -->
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Accueil
            </a>
            <a href="${pageContext.request.contextPath}/main/reservations" class="btn btn-sm ms-2">
                <i class="fas fa-calendar-alt me-1"></i> Toutes les réservations
            </a>
            <a href="${pageContext.request.contextPath}/main/ajouterPaiement" class="btn btn-sm ms-2">
                <i class="fas fa-plus-circle me-1"></i> Ajouter un paiement
            </a>
        </div>

        <!-- Formulaire container -->
        <div class="form-container">
            <!-- En-tête -->
            <div class="form-header">
                <h4>
                    <i class="fas fa-money-check-edit me-2"></i>Modifier le Mode de Paiement
                </h4>
                <p class="mb-0" style="opacity: 0.9;">Mettre à jour le mode de paiement d'une réservation payée</p>
            </div>
            
            <!-- Message -->
            <c:if test="${not empty message}">
                <div class="alert alert-${typeMessage == 'success' ? 'success' : (typeMessage == 'warning' ? 'warning' : 'danger')} alert-dismissible fade show">
                    <i class="fas fa-${typeMessage == 'success' ? 'check-circle' : (typeMessage == 'warning' ? 'exclamation-triangle' : 'exclamation-circle')} me-2"></i>
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${not empty reservationsPayees and not reservationsPayees.isEmpty()}">
                    <!-- Formulaire (seulement si des réservations payées existent) -->
                    <form action="${pageContext.request.contextPath}/main/modifierPaiement" method="POST">
                        
                        <!-- Sélection de la réservation -->
                        <div class="mb-4">
                            <label class="form-label mb-3">
                                <i class="fas fa-calendar-check me-1"></i>Sélectionnez une réservation payée
                            </label>
                            
                            <select class="form-select form-select-lg" 
                                    name="idReservation" 
                                    id="reservationSelect"
                                    required
                                    onchange="updateReservationDetails()">
                                <option value="">-- Sélectionnez une réservation --</option>
                                <c:forEach var="reservation" items="${reservationsPayees}">
                                    <option value="${reservation.idReservation}" 
                                            data-client="${reservation.client.nom}"
                                            data-terrain="${reservation.terrain.nom}"
                                            data-mode="${reservation.modePaiement}">
                                        ID: ${reservation.idReservation} | 
                                        Client: ${reservation.client.nom} | 
                                        Terrain: ${reservation.terrain.nom}
                                    </option>
                                </c:forEach>
                            </select>
                            
                            <div class="form-text mt-2">
                                <i class="fas fa-info-circle me-1"></i>
                                ${reservationsPayees.size()} réservation(s) payée(s) disponible(s).
                            </div>
                        </div>
                        
                        <!-- Détails de la réservation sélectionnée -->
                        <div id="reservationDetails" class="selected-reservation" style="display: none;">
                            <h6 class="mb-3" style="color: var(--lilas-fonce);">
                                <i class="fas fa-info-circle me-2"></i>Détails de la réservation
                            </h6>
                            <div class="reservation-detail">
                                <span class="reservation-label">ID Réservation:</span>
                                <span id="detailId" class="fw-bold">-</span>
                            </div>
                            <div class="reservation-detail">
                                <span class="reservation-label">Client:</span>
                                <span id="detailClient">-</span>
                            </div>
                            <div class="reservation-detail">
                                <span class="reservation-label">Terrain:</span>
                                <span id="detailTerrain">-</span>
                            </div>
                            <div class="reservation-detail">
                                <span class="reservation-label">Mode actuel:</span>
                                <span id="detailCurrentMode" class="fw-bold">-</span>
                            </div>
                        </div>
                        
                        <!-- Mode de paiement -->
                        <div class="mb-4 mt-4">
                            <label class="form-label mb-3">
                                <i class="fas fa-credit-card me-1"></i>Nouveau mode de paiement
                            </label>
                            
                            <div class="mode-option" onclick="selectMode('virement')">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" 
                                           name="modePaiement" value="virement" id="virement" required>
                                    <label class="form-check-label fw-medium" for="virement">
                                        <span class="mode-icon">🏦</span>
                                        Virement bancaire
                                    </label>
                                </div>
                            </div>
                            
                            <div class="mode-option" onclick="selectMode('carte')">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" 
                                           name="modePaiement" value="carte" id="carte">
                                    <label class="form-check-label fw-medium" for="carte">
                                        <span class="mode-icon">💳</span>
                                        Carte bancaire
                                    </label>
                                </div>
                            </div>
                            
                            <div class="mode-option" onclick="selectMode('especes')">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" 
                                           name="modePaiement" value="especes" id="especes">
                                    <label class="form-check-label fw-medium" for="especes">
                                        <span class="mode-icon">💵</span>
                                        Espèces
                                    </label>
                                </div>
                            </div>
                            
                            
                            
                            
                        
                        <!-- Boutons -->
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg py-3">
                                <i class="fas fa-save me-2"></i>Mettre à jour le paiement
                            </button>
                         
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <!-- Message si aucune réservation payée -->
                    <div class="no-data">
                        <i class="fas fa-money-check-alt"></i>
                        <h4 class="mb-3">Aucune réservation payée disponible</h4>
                        <p class="mb-4">Pour modifier un mode de paiement, vous devez d'abord avoir des réservations avec paiement confirmé.</p>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/main/ajouterPaiement" class="btn btn-primary btn-lg py-3">
                                <i class="fas fa-plus-circle me-2"></i>Ajouter un paiement
                            </a>
                            <a href="${pageContext.request.contextPath}/main/reservations" class="btn btn-outline-secondary py-3">
                                <i class="fas fa-calendar-alt me-2"></i>Voir les réservations
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Mettre à jour les détails de la réservation sélectionnée
        function updateReservationDetails() {
            const select = document.getElementById('reservationSelect');
            const selectedOption = select.options[select.selectedIndex];
            const detailsDiv = document.getElementById('reservationDetails');
            
            if (selectedOption.value !== "") {
                // Afficher les détails
                detailsDiv.style.display = 'block';
                document.getElementById('detailId').textContent = selectedOption.value;
                document.getElementById('detailClient').textContent = selectedOption.getAttribute('data-client');
                document.getElementById('detailTerrain').textContent = selectedOption.getAttribute('data-terrain');
                document.getElementById('detailCurrentMode').textContent = selectedOption.getAttribute('data-mode');
            } else {
                // Cacher les détails
                detailsDiv.style.display = 'none';
            }
        }
        
        // Sélection du mode de paiement
        function selectMode(mode) {
            // Désélectionner tout
            document.querySelectorAll('.mode-option').forEach(option => {
                option.classList.remove('selected');
            });
            
            // Sélectionner celui cliqué
            event.currentTarget.classList.add('selected');
            
            // Cocher le radio
            document.getElementById(mode).checked = true;
        }
        
        // Gestion de la sélection au chargement
        document.addEventListener('DOMContentLoaded', function() {
            // Si un mode est déjà sélectionné, mettre à jour l'UI
            const checkedRadio = document.querySelector('input[name="modePaiement"]:checked');
            if (checkedRadio) {
                const modeOption = checkedRadio.closest('.mode-option');
                if (modeOption) {
                    modeOption.classList.add('selected');
                }
            }
            
            // Ajouter l'événement click aux options de mode
            document.querySelectorAll('.mode-option').forEach(option => {
                option.addEventListener('click', function(e) {
                    if (e.target.type !== 'radio' && e.target.type !== 'checkbox') {
                        const radio = this.querySelector('input[type="radio"]');
                        if (radio) {
                            radio.checked = true;
                            selectMode(radio.value);
                        }
                    }
                });
            });
            
            // Initialiser les détails si une réservation est déjà sélectionnée
            updateReservationDetails();
        });
        
        // Confirmation avant envoi
        const form = document.querySelector('form');
        if (form) {
            form.addEventListener('submit', function(e) {
                const select = document.getElementById('reservationSelect');
                const selectedOption = select.options[select.selectedIndex];
                const mode = document.querySelector('input[name="modePaiement"]:checked');
                
                if (!selectedOption.value) {
                    alert('Veuillez sélectionner une réservation');
                    e.preventDefault();
                    return;
                }
                
                if (!mode) {
                    alert('Veuillez sélectionner un mode de paiement');
                    e.preventDefault();
                    return;
                }
                
                const modeText = {
                    'virement': 'Virement bancaire 🏦',
                    'carte': 'Carte bancaire 💳', 
                    'especes': 'Espèces 💵',
                    
                }[mode.value];
                
                if (!confirm(`Confirmer la modification ?\n\n📋 Réservation ID: ${selectedOption.value}\n👤 Client: ${selectedOption.getAttribute('data-client')}\n🏟️ Terrain: ${selectedOption.getAttribute('data-terrain')}\n💳 Mode actuel: ${selectedOption.getAttribute('data-mode')}\n🔄 Nouveau mode: ${modeText}`)) {
                    e.preventDefault();
                }
            });
        }
    </script>
</body>
</html>