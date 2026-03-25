<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Montant Total par Client</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
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
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(138, 75, 175, 0.1);
            border: 1px solid var(--lilas-clair);
        }
        
        .page-header {
            background-color: var(--lilas);
            color: white;
            border-radius: 6px;
            padding: 12px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .table th {
            background-color: var(--lilas-clair);
            color: var(--lilas-fonce);
            border-bottom: 2px solid var(--lilas);
            font-weight: 600;
        }
        
        .table td {
            vertical-align: middle;
        }
        
        .table-striped tbody tr:nth-of-type(odd) {
            background-color: rgba(138, 75, 175, 0.03);
        }
        
        .btn-outline-lilas {
            border-color: var(--lilas);
            color: var(--lilas);
        }
        
        .btn-outline-lilas:hover {
            background-color: var(--lilas);
            color: white;
        }
        
        .btn-lilas {
            background-color: var(--lilas);
            border-color: var(--lilas);
            color: white;
        }
        
        .btn-lilas:hover {
            background-color: var(--lilas-fonce);
            border-color: var(--lilas-fonce);
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #6c757d;
        }
        
        .empty-state h5 {
            color: var(--lilas-fonce);
            margin-bottom: 10px;
        }
        
        .montant {
            color: var(--lilas-fonce);
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="page-header">
            <h5 class="mb-2">Montant Total par Client</h5>
            <p class="mb-0">Classement des clients par montant total dépensé (trié par montant décroissant)</p>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-outline-lilas btn-sm">
                Accueil
            </a>
            <a href="${pageContext.request.contextPath}/main/paiements" class="btn btn-outline-lilas btn-sm ms-2">
                Tous les paiements
            </a>
        </div>

        <!-- Vérification des données -->
        <c:choose>
            <c:when test="${empty montantsClients}">
                <div class="empty-state">
                    <h5 class="mt-3 mb-2">Aucune donnée financière</h5>
                    <p class="text-muted mb-4">Aucun paiement n'a encore été enregistré pour les clients.</p>
                    <a href="${pageContext.request.contextPath}/main/ajouterPaiement" 
                       class="btn btn-lilas">
                        Enregistrer un paiement
                    </a>
                </div>
            </c:when>
            
            <c:otherwise>
                <!-- Tableau des montants -->
                <div class="card shadow-sm border-0">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th width="80" class="text-center">#</th>
                                        <th>Client</th>
                                        <th>Email</th>
                                        <th class="text-end">Montant total</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="client" items="${montantsClients}" varStatus="status">
                                        <c:set var="montant" value="${client[2] != null ? client[2] : 0}" />
                                        <tr>
                                            <td class="text-center text-muted">
                                                ${status.index + 1}
                                            </td>
                                            <td>
                                                <strong>${client[0]}</strong>
                                            </td>
                                            <td>
                                                <small class="text-muted">${client[1]}</small>
                                            </td>
                                            <td class="text-end montant">
                                                <fmt:formatNumber value="${montant}" 
                                                                  type="currency" 
                                                                  currencySymbol="€" 
                                                                  maxFractionDigits="2" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Information -->
                        <div class="p-3 border-top">
                            <small class="text-muted">
                                ${montantsClients.size()} client(s) listé(s)
                            </small>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>