<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Terrains Sportifs - Clients</title>
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
            max-width: 1200px;
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
            padding: 20px;
            margin-bottom: 25px;
            text-align: center;
        }
        
        .btn-action {
            background-color: var(--lilas);
            color: white;
            border: none;
            padding: 8px 15px;
            font-weight: 500;
            border-radius: 6px;
            transition: background-color 0.3s;
        }
        
        .btn-action:hover {
            background-color: var(--lilas-fonce);
        }
        
        .section-title {
            color: var(--lilas-fonce);
            border-left: 4px solid var(--lilas);
            padding-left: 12px;
            margin: 25px 0 15px 0;
            font-weight: 600;
        }
        
        .table-header {
            background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce));
            color: white;
        }
        
        .table th {
            background-color: var(--lilas-clair);
            color: var(--lilas-fonce);
            border-top: none;
        }
        
        .table-hover tbody tr:hover {
            background-color: rgba(138, 75, 175, 0.05);
        }
        
        .badge-client {
            background-color: var(--lilas);
            color: white;
        }
        
        .btn-delete {
            color: #dc3545;
            border: 1px solid #dc3545;
            background: transparent;
        }
        
        .btn-delete:hover {
            background-color: #dc3545;
            color: white;
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
                <i class="fas fa-users me-2"></i>Gestion des Clients
            </h5>
            <p class="mb-0">Liste des clients enregistrés dans le système</p>
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
                <h6 class="section-title">
                    <i class="fas fa-list me-2"></i>Tous les clients
                </h6>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/main/ajouterClient" 
                   class="btn btn-action me-2">
                    <i class="fas fa-user-plus me-1"></i> Nouveau Client
                </a>
                <a href="${pageContext.request.contextPath}/main/" 
                   class="btn btn-outline-secondary">
                    <i class="fas fa-home me-1"></i> Accueil
                </a>
            </div>
        </div>
        
        <!-- Tableau des clients -->
        <div class="card shadow-sm border-0">
            <div class="card-header table-header">
                <h6 class="mb-0">
                    <i class="fas fa-table me-2"></i> Liste des clients
                </h6>
            </div>
            <div class="card-body p-0">
                <c:if test="${not empty clients}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th width="80">ID</th>
                                    <th>Nom</th>
                                    <th>Email</th>
                                    <th>Téléphone</th>
                                    <th width="120">Date Inscription</th>
                                    <th width="100" class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="client" items="${clients}">
                                    <tr>
                                        <td>
                                            <span class="badge badge-client">#${client.idClient}</span>
                                        </td>
                                        <td><strong>${client.nom}</strong></td>
                                        <td>${client.email}</td>
                                        <td>${client.telephone}</td>
                                        <td>
                                            <fmt:formatDate value="${client.dateInscription}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td class="text-center">
                                            <!-- Formulaire de suppression -->
                                            <form action="${pageContext.request.contextPath}/main/supprimerClient" 
                                                  method="post" 
                                                  id="deleteForm-${client.idClient}"
                                                  style="display: inline;">
                                                <input type="hidden" name="id" value="${client.idClient}">
                                                <button type="button" 
                                                        class="btn btn-sm btn-delete"
                                                        onclick="confirmDelete(${client.idClient}, '${client.nom}')"
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
                    
                    <!-- Statistique -->
                    <div class="p-3 border-top">
                        <div class="alert alert-primary mb-0">
                            <i class="fas fa-chart-bar me-2"></i>
                            Total : <strong>${clients.size()}</strong> client(s)
                        </div>
                    </div>
                </c:if>
                <c:if test="${empty clients}">
                    <div class="text-center py-5">
                        <i class="fas fa-users fa-4x text-lilas-clair mb-3"></i>
                        <h4 class="text-lilas-fonce">Aucun client enregistré</h4>
                        <p class="text-muted mb-4">Commencez par ajouter votre premier client au système.</p>
                        <a href="${pageContext.request.contextPath}/main/ajouterClient" 
                           class="btn btn-action">
                            <i class="fas fa-user-plus me-1"></i> Ajouter un client
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
                    <p>Êtes-vous sûr de vouloir supprimer le client :</p>
                    <p class="text-center fw-bold" id="clientName"></p>
                    <p class="text-muted small">Cette action est irréversible. Toutes les réservations et paiements associés à ce client seront également supprimés.</p>
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
        function confirmDelete(clientId, clientName) {
            document.getElementById('clientName').textContent = clientName;
            currentFormId = 'deleteForm-' + clientId;
            
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
            const badges = document.querySelectorAll('.badge-client');
            badges.forEach((badge, index) => {
                badge.style.animationDelay = (index * 0.1) + 's';
            });
        });
    </script>
</body>
</html>