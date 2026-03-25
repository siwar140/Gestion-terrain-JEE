<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Clients Football</title>
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
        
        .badge-lilas {
            background-color: var(--lilas);
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
                <i class="fas fa-futbol me-2"></i>Clients Football
            </h5>
            <p class="mb-0">Clients ayant réservé un terrain de football</p>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Retour
            </a>
            <span class="badge badge-lilas float-end">
                ${not empty clientsFootball ? clientsFootball.size() : 0} clients
            </span>
        </div>

        <!-- Tableau -->
        <c:if test="${not empty clientsFootball}">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Nom</th>
                            <th>Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="client" items="${clientsFootball}">
                            <tr>
                                <td>
                                    <strong>${client[0]}</strong>
                                </td>
                                <td>
                                    ${client[1]}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        
        <c:if test="${empty clientsFootball}">
            <div class="empty-state">
                <i class="fas fa-futbol"></i>
                <h5 class="mt-3 mb-2">Aucun client trouvé</h5>
                <p class="text-muted mb-4">Aucun client n'a encore réservé de terrain de football.</p>
                <a href="${pageContext.request.contextPath}/main/ajouterReservation" 
                   class="btn" style="background-color: var(--lilas); color: white;">
                    <i class="fas fa-calendar-plus me-1"></i> Créer une réservation
                </a>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>