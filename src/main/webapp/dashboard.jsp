<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tableau de Bord</title>
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
        
        .stat-card {
            border-left: 4px solid var(--lilas);
            border-radius: 8px;
            margin-bottom: 15px;
        }
        
        .stat-number {
            font-size: 2em;
            font-weight: 600;
            color: var(--lilas-fonce);
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
        
        .btn-secondary {
            background-color: #f0f0f0;
            color: var(--lilas-fonce);
            border: 1px solid var(--lilas-clair);
        }
        
        .feature-item {
            border: 1px solid var(--lilas-clair);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            transition: all 0.3s;
        }
        
        .feature-item:hover {
            background-color: rgba(138, 75, 175, 0.05);
        }
        
        .feature-icon {
            color: var(--lilas);
            font-size: 1.5em;
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-2">
                <i class="fas fa-tachometer-alt me-2"></i>Tableau de Bord
            </h5>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Accueil
            </a>
        </div>

        <!-- Statistiques -->
        <h6 class="mb-3" style="color: var(--lilas-fonce);">
            <i class="fas fa-chart-bar me-2"></i>Statistiques
        </h6>
        
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card stat-card">
                    <div class="card-body">
                        <div class="text-muted small">Clients</div>
                        <div class="stat-number">${nombreClients}</div>
                        <a href="${pageContext.request.contextPath}/main/clients" 
                           class="btn btn-sm btn-action mt-2">
                            Voir
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card stat-card">
                    <div class="card-body">
                        <div class="text-muted small">Terrains</div>
                        <div class="stat-number">${nombreTerrains}</div>
                        <a href="${pageContext.request.contextPath}/main/terrains" 
                           class="btn btn-sm btn-action mt-2">
                            Voir
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card stat-card">
                    <div class="card-body">
                        <div class="text-muted small">Réservations</div>
                        <div class="stat-number">${nombreReservations}</div>
                        <a href="${pageContext.request.contextPath}/main/reservations" 
                           class="btn btn-sm btn-action mt-2">
                            Voir
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card stat-card">
                    <div class="card-body">
                        <div class="text-muted small">Paiements</div>
                        <div class="stat-number">${nombrePaiements}</div>
                        <a href="${pageContext.request.contextPath}/main/paiements" 
                           class="btn btn-sm btn-action mt-2">
                            Voir
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Ajout rapide -->
        <h6 class="mb-3 mt-4" style="color: var(--lilas-fonce);">
            <i class="fas fa-plus-circle me-2"></i>Ajout rapide
        </h6>
        
        <div class="row mb-4">
            <div class="col-md-3 col-6 mb-2">
                <a href="${pageContext.request.contextPath}/main/ajouterClient" 
                   class="btn btn-action w-100">
                    <i class="fas fa-user-plus me-1"></i> Client
                </a>
            </div>
            <div class="col-md-3 col-6 mb-2">
                <a href="${pageContext.request.contextPath}/main/ajouterTerrain" 
                   class="btn btn-action w-100">
                    <i class="fas fa-plus me-1"></i> Terrain
                </a>
            </div>
            <div class="col-md-3 col-6 mb-2">
                <a href="${pageContext.request.contextPath}/main/ajouterReservation" 
                   class="btn btn-action w-100">
                    <i class="fas fa-calendar-plus me-1"></i> Réservation
                </a>
            </div>
            <div class="col-md-3 col-6 mb-2">
                <a href="${pageContext.request.contextPath}/main/ajouterPaiement" 
                   class="btn btn-action w-100">
                    <i class="fas fa-money-bill me-1"></i> Paiement
                </a>
            </div>
        </div>

        <!-- Fonctionnalités -->
        <h6 class="mb-3 mt-4" style="color: var(--lilas-fonce);">
            <i class="fas fa-tasks me-2"></i>Fonctionnalités
        </h6>
        
        <div class="row">
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-football-ball feature-icon"></i>
                            <span>Clients Football</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/clientsFootball" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-calendar feature-icon"></i>
                            <span>Réservations Mars 2025</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/reservationsMars2025" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-chart-bar feature-icon"></i>
                            <span>Réservations par terrain</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/reservationsParTerrain" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-map-marker-alt feature-icon"></i>
                            <span>Terrains non réservés</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/terrainsNonReserves" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-money-check-alt feature-icon"></i>
                            <span>Montant par client</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/montantTotal" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-exchange-alt feature-icon"></i>
                            <span>Modifier paiement #202</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/modifierPaiement202" 
                           class="btn btn-sm btn-secondary">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Action administrative -->
        <div class="mt-4 pt-3 border-top">
            <div class="feature-item" style="border-color: #dc3545;">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <i class="fas fa-trash feature-icon" style="color: #dc3545;"></i>
                        <span>Supprimer réservations anciennes</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/main/supprimerReservationsAvant" 
                       class="btn btn-sm" style="background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb;">
                        Accéder
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>