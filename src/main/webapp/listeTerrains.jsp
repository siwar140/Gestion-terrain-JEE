<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Terrains</title>
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
        
        .badge-id {
            background-color: var(--lilas);
            color: white;
        }
        
        .badge-capacite {
            background-color: #6c757d;
            color: white;
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
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-2">
                <i class="fas fa-futbol me-2"></i>Gestion des Terrains
            </h5>
            <p class="mb-0">Liste des terrains disponibles dans le système</p>
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
                    <i class="fas fa-list me-2"></i>Liste des terrains
                </h6>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/main/ajouterTerrain" 
                   class="btn btn-submit me-2">
                    <i class="fas fa-plus-circle me-1"></i> Nouveau Terrain
                </a>
                <a href="${pageContext.request.contextPath}/main/" 
                   class="btn btn-secondary">
                    <i class="fas fa-home me-1"></i> Accueil
                </a>
            </div>
        </div>

        <!-- Tableau des terrains -->
        <div class="card shadow-sm border-0">
            <div class="card-header" style="background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce)); color: white;">
                <h6 class="mb-0">
                    <i class="fas fa-table me-2"></i> Terrains enregistrés
                </h6>
            </div>
            <div class="card-body p-0">
                <c:if test="${not empty terrains}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th width="80">ID</th>
                                    <th>Nom</th>
                                    <th>Type</th>
                                    <th>Capacité</th>
                                    <th width="100" class="text-center">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="terrain" items="${terrains}">
                                    <tr>
                                        <td>
                                            <span class="badge badge-id">#${terrain.idTerrain}</span>
                                        </td>
                                        <td>
                                            <strong>${terrain.nom}</strong>
                                        </td>
                                        <td>${terrain.type}</td>
                                        <td>
                                            <span class="badge badge-capacite">
                                                ${terrain.capacite} joueurs
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <!-- Formulaire de suppression -->
                                            <form action="${pageContext.request.contextPath}/main/supprimerTerrain" 
                                                  method="post" 
                                                  id="deleteForm-${terrain.idTerrain}"
                                                  style="display: inline;">
                                                <input type="hidden" name="id" value="${terrain.idTerrain}">
                                                <button type="button" 
                                                        class="btn btn-sm btn-outline-danger"
                                                        onclick="confirmDelete(${terrain.idTerrain}, '${terrain.nom}')"
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
                        <div class="alert" style="background-color: var(--lilas-clair); color: var(--lilas-fonce);">
                            <i class="fas fa-chart-bar me-2"></i>
                            Total : <strong>${terrains.size()}</strong> terrain(s)
                        </div>
                    </div>
                </c:if>
                <c:if test="${empty terrains}">
                    <div class="empty-state">
                        <i class="fas fa-futbol"></i>
                        <h5 class="mt-3 mb-2">Aucun terrain enregistré</h5>
                        <p class="text-muted mb-4">Commencez par ajouter votre premier terrain.</p>
                        <a href="${pageContext.request.contextPath}/main/ajouterTerrain" 
                           class="btn btn-submit">
                            <i class="fas fa-plus-circle me-1"></i> Ajouter un terrain
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
                    <p>Êtes-vous sûr de vouloir supprimer le terrain :</p>
                    <p class="text-center fw-bold" id="terrainInfo"></p>
                    <p class="text-muted small">Cette action est irréversible. Toutes les réservations et paiements associés à ce terrain seront également supprimés.</p>
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
        function confirmDelete(terrainId, terrainName) {
            document.getElementById('terrainInfo').textContent = terrainName;
            currentFormId = 'deleteForm-' + terrainId;
            
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