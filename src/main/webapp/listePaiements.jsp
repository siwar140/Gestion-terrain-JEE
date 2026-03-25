<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Paiements</title>
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
            max-width: 1400px;
            margin-top: 20px;
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
        
        .table th {
            background-color: var(--lilas-clair);
            color: var(--lilas-fonce);
            border-bottom: 2px solid var(--lilas);
        }
        
        .table-hover tbody tr:hover {
            background-color: rgba(138, 75, 175, 0.05);
        }
        
        .badge-mode-carte {
            background-color: #0d6efd;
            color: white;
        }
        
        .badge-mode-especes {
            background-color: #198754;
            color: white;
        }
        
        .badge-mode-virement {
            background-color: #ffc107;
            color: #212529;
        }
        
        .badge-id {
            background-color: var(--lilas);
            color: white;
        }
        
        .amount-positive {
            color: #198754;
            font-weight: 600;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 3em;
            margin-bottom: 15px;
            color: var(--lilas-clair);
        }
        
        .modal-confirm {
            color: #636363;
        }
        
        .modal-confirm .modal-content {
            padding: 20px;
            border-radius: 8px;
            border: none;
        }
        
        .modal-confirm .modal-header {
            border-bottom: none;
            position: relative;
        }
        
        .modal-confirm h4 {
            text-align: center;
            font-size: 26px;
            margin: 30px 0 -15px;
        }
        
        .modal-confirm .modal-body {
            color: #999;
        }
        
        .modal-confirm .modal-footer {
            border: none;
            text-align: center;
            border-radius: 5px;
            font-size: 13px;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-2">
                <i class="fas fa-money-check-alt me-2"></i>Gestion des Paiements
            </h5>
            <p class="mb-0">Liste des paiements enregistrés dans le système</p>
        </div>

        <!-- Messages -->
        <c:if test="${not empty message}">
            <div class="alert alert-${typeMessage == 'success' ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
                <i class="fas ${typeMessage == 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Boutons d'action -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h6 style="color: var(--lilas-fonce); border-left: 4px solid var(--lilas); padding-left: 12px;">
                    <i class="fas fa-list me-2"></i>Liste des paiements
                </h6>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/main/ajouterPaiement" 
                   class="btn btn-submit me-2">
                    <i class="fas fa-plus-circle me-1"></i> Nouveau Paiement
                </a>
                <a href="${pageContext.request.contextPath}/main/" 
                   class="btn btn-secondary">
                    <i class="fas fa-home me-1"></i> Accueil
                </a>
            </div>
        </div>

        <!-- Tableau des paiements -->
        <div class="card shadow-sm border-0">
            <div class="card-header" style="background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce)); color: white;">
                <h6 class="mb-0">
                    <i class="fas fa-table me-2"></i> Paiements enregistrés
                </h6>
            </div>
            <div class="card-body p-0">
                <c:if test="${not empty paiements}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th width="80">ID</th>
                                    <th>Date</th>
                                    <th>Montant</th>
                                    <th>Mode</th>
                                    <th>Client</th>
                                    <th>Réservation</th>
                                    <th width="100" class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="paiement" items="${paiements}">
                                    <tr>
                                        <td>
                                            <span class="badge badge-id">#${paiement.idPaiement}</span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${paiement.datePaiement}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td class="amount-positive">
                                            <fmt:formatNumber value="${paiement.montant}" 
                                                              type="currency" currencySymbol="€" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${paiement.modePaiement == 'carte' || paiement.modePaiement == 'Carte Bancaire'}">
                                                    <span class="badge badge-mode-carte">
                                                        <i class="fas fa-credit-card me-1"></i>Carte
                                                    </span>
                                                </c:when>
                                                <c:when test="${paiement.modePaiement == 'especes' || paiement.modePaiement == 'Espèces'}">
                                                    <span class="badge badge-mode-especes">
                                                        <i class="fas fa-money-bill-wave me-1"></i>Especes
                                                    </span>
                                                </c:when>
                                                <c:when test="${paiement.modePaiement == 'virement' || paiement.modePaiement == 'Virement'}">
                                                    <span class="badge badge-mode-virement">
                                                        <i class="fas fa-university me-1"></i>Virement
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${paiement.modePaiement}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if test="${not empty paiement.client}">
                                                <strong>${paiement.client.nom}</strong>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty paiement.reservation}">
                                                <span class="badge bg-secondary">
                                                    #${paiement.reservation.idReservation}
                                                </span>
                                            </c:if>
                                        </td>
                                        <td class="text-center">
                                            <!-- Formulaire de suppression -->
                                            <form action="${pageContext.request.contextPath}/main/supprimerPaiement" 
                                                  method="post" 
                                                  id="deleteForm-${paiement.idPaiement}"
                                                  style="display: inline;">
                                                <input type="hidden" name="id" value="${paiement.idPaiement}">
                                                <button type="button" 
                                                        class="btn btn-sm btn-outline-danger"
                                                        onclick="confirmDelete(${paiement.idPaiement}, '${paiement.montant}€')"
                                                        title="Supprimer">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Statistique simple -->
                    <div class="p-3 border-top">
                        <div class="alert" style="background-color: var(--lilas-clair); color: var(--lilas-fonce);">
                            <i class="fas fa-chart-bar me-2"></i>
                            Total : <strong>${paiements.size()}</strong> paiement(s)
                        </div>
                    </div>
                </c:if>
                <c:if test="${empty paiements}">
                    <div class="empty-state">
                        <i class="fas fa-money-bill-wave"></i>
                        <h5 class="mt-3 mb-2">Aucun paiement enregistré</h5>
                        <p class="text-muted mb-4">Commencez par enregistrer votre premier paiement au système.</p>
                        <a href="${pageContext.request.contextPath}/main/ajouterPaiement" 
                           class="btn btn-submit">
                            <i class="fas fa-plus-circle me-1"></i> Ajouter un paiement
                        </a>
                    </div>
                </c:if>
            </div>
        </div>

       

    <!-- Modal de confirmation de suppression -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title text-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>Confirmer la suppression
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Êtes-vous sûr de vouloir supprimer le paiement :</p>
                    <p class="text-center fw-bold" id="paiementInfo"></p>
                    <p class="text-muted small">Cette action est irréversible. Les données financières seront mises à jour.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i> Annuler
                    </button>
                    <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
                        <i class="fas fa-trash me-1"></i> Confirmer
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let currentFormId = null;
        
        // Fonction pour afficher le modal de confirmation
        function confirmDelete(paiementId, montant) {
            document.getElementById('paiementInfo').textContent = 'Paiement #' + paiementId + ' - ' + montant;
            currentFormId = 'deleteForm-' + paiementId;
            
            // Afficher le modal
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
        
        // Gérer la confirmation de suppression
        document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
            if (currentFormId) {
                document.getElementById(currentFormId).submit();
            }
        });
        
        // Animation pour les badges
        document.addEventListener('DOMContentLoaded', function() {
            const badges = document.querySelectorAll('.badge-id');
            badges.forEach((badge, index) => {
                badge.style.animationDelay = (index * 0.1) + 's';
            });
        });
    </script>
</body>
</html> 