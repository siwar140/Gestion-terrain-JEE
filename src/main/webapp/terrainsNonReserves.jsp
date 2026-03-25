<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Terrains Non Réservés</title>
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
            max-width: 1000px;
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
        
        .badge-non-reserve {
            background-color: #ffc107;
            color: #212529;
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
        
        .terrain-badge {
            background-color: var(--lilas);
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 0.8em;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-2">
                <i class="fas fa-map-marker-alt me-2"></i>Terrains Non Réservés
            </h5>
            <p class="mb-0">Liste des terrains qui n'ont jamais été réservés</p>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Accueil
            </a>
            <a href="${pageContext.request.contextPath}/main/terrains" class="btn btn-secondary btn-sm ms-2">
                <i class="fas fa-futbol me-1"></i> Tous les terrains
            </a>
        </div>

        <!-- Tableau des terrains non réservés -->
        <div class="card shadow-sm border-0">
            <div class="card-header" style="background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce)); color: white;">
                <h6 class="mb-0">
                    <i class="fas fa-table me-2"></i> Terrains non réservés
                    <c:if test="${not empty terrainsNonReserves}">
                        <span class="badge bg-light text-dark ms-2">${terrainsNonReserves.size()}</span>
                    </c:if>
                </h6>
            </div>
            <div class="card-body p-0">
                <c:if test="${not empty terrainsNonReserves}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th width="60">#</th>
                                    <th>Nom du terrain</th>
                                    <th>Localisation</th>
                                    <th>Statut</th>
                                    <th width="150">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="terrain" items="${terrainsNonReserves}" varStatus="status">
                                    <tr>
                                        <td>
                                            <span class="terrain-badge">${status.index + 1}</span>
                                        </td>
                                        <td>
                                            <strong>${terrain[0]}</strong>
                                        </td>
                                        <td>
                                            ${terrain[1]}
                                        </td>
                                        <td>
                                            <span class="badge badge-non-reserve">
                                                <i class="fas fa-clock me-1"></i> Jamais réservé
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/main/ajouterReservation" 
                                               class="btn btn-sm" style="background-color: var(--lilas); color: white;">
                                                <i class="fas fa-calendar-plus me-1"></i> Réserver
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                
                <c:if test="${empty terrainsNonReserves}">
                    <div class="empty-state">
                        <i class="fas fa-check-circle"></i>
                        <h5 class="mt-3 mb-2">Tous les terrains ont été réservés</h5>
                        <p class="text-muted mb-4">Excellent travail !</p>
                        <a href="${pageContext.request.contextPath}/main/ajouterTerrain" 
                           class="btn" style="background-color: var(--lilas); color: white;">
                            <i class="fas fa-plus-circle me-1"></i> Ajouter un terrain
                        </a>
                    </div>
                </c:if>
            </div>
        </div>

        
</body>
</html>