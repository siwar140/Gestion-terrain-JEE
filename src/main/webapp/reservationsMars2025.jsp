<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Réservations Mars 2025</title>
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
        
        .weekend-badge {
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
        
        .month-header {
            background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce));
            color: white;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            margin-bottom: 20px;
        }
        
        .payment-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.8em;
        }
        
        .paid-badge {
            background-color: #28a745;
            color: white;
        }
        
        .unpaid-badge {
            background-color: #dc3545;
            color: white;
        }
        
        .terrain-badge {
            background-color: #6c757d;
            color: white;
            padding: 3px 6px;
            border-radius: 3px;
            font-size: 0.75em;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-2">
                <i class="fas fa-calendar-alt me-2"></i>Réservations Mars 2025
            </h5>
            <p class="mb-0">Liste des réservations pour le mois de mars 2025</p>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Accueil
            </a>
            <a href="${pageContext.request.contextPath}/main/reservations" class="btn btn-secondary btn-sm ms-2">
                <i class="fas fa-list me-1"></i> Toutes les réservations
            </a>
        </div>

        <!-- En-tête du mois -->
        <div class="month-header">
            <h4 class="mb-0">Mars 2025</h4>
            <small>Période du 1er au 31 Mars 2025</small>
        </div>

        <!-- Tableau des réservations -->
        <div class="card shadow-sm border-0">
            <div class="card-header" style="background: linear-gradient(45deg, var(--lilas), var(--lilas-fonce)); color: white;">
                <h6 class="mb-0">
                    <i class="fas fa-table me-2"></i> Liste des réservations
                </h6>
            </div>
            <div class="card-body p-0">
                <c:if test="${not empty reservations}">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th width="80">ID</th>
                                    <th>Date</th>
                                    <th>Client</th>
                                    <th>Terrain</th>
                                    <th>Type</th>
                                    <th>Paiement</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="reservation" items="${reservations}">
                                    <tr>
                                        <td>
                                            <span class="badge badge-id">#${reservation.idReservation}</span>
                                        </td>
                                        <td>
                                            <strong>
                                                <fmt:formatDate value="${reservation.dateReservation}" pattern="dd/MM/yyyy" />
                                            </strong>
                                        </td>
                                        <td>
                                            <c:if test="${not empty reservation.client}">
                                                ${reservation.client.nom}
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty reservation.terrain}">
                                                ${reservation.terrain.nom}
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty reservation.terrain}">
                                                <span class="terrain-badge">
                                                    ${reservation.terrain.type}
                                                </span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty reservation.modePaiement}">
                                                    <span class="payment-badge paid-badge">
                                                        ${reservation.modePaiement}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="payment-badge unpaid-badge">
                                                        Non payé
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Nombre total -->
                    <div class="p-3 border-top">
                        <div class="text-muted">
                            <i class="fas fa-info-circle me-1"></i>
                            Total : <strong>${reservations.size()}</strong> réservation(s)
                        </div>
                    </div>
                </c:if>
                
                <c:if test="${empty reservations}">
                    <div class="empty-state">
                        <i class="fas fa-calendar-times"></i>
                        <h5 class="mt-3 mb-2">Aucune réservation en Mars 2025</h5>
                        <p class="text-muted mb-4">Aucune réservation n'a été enregistrée pour cette période.</p>
                        <a href="${pageContext.request.contextPath}/main/ajouterReservation" 
                           class="btn" style="background-color: var(--lilas); color: white;">
                            <i class="fas fa-calendar-plus me-1"></i> Créer une réservation
                        </a>
                    </div>
                </c:if>
            </div>
        </div>

        

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Simple effet hover sur les lignes
        document.addEventListener('DOMContentLoaded', function() {
            const rows = document.querySelectorAll('tbody tr');
            rows.forEach(row => {
                row.addEventListener('mouseenter', function() {
                    this.style.backgroundColor = 'rgba(138, 75, 175, 0.05)';
                });
                row.addEventListener('mouseleave', function() {
                    this.style.backgroundColor = '';
                });
            });
        });
    </script>
</body>
</html>