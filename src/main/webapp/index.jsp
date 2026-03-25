<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestion des Terrains Sportifs</title>
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
                <i class="fas fa-futbol me-2"></i>Gestion des Terrains Sportifs
            </h5>
            <p class="mb-0">Système de gestion des réservations</p>
        </div>

        <!-- Gestion Standard -->
        <h6 class="section-title">
            <i class="fas fa-cogs me-2"></i>Gestion
        </h6>
        
        <div class="row mb-4">
            <div class="col-md-3 col-6 mb-3">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-users feature-icon"></i>
                            <span>Clients</span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/main/clients" 
                               class="btn btn-sm btn-action me-1">Liste</a>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3 col-6 mb-3">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-futbol feature-icon"></i>
                            <span>Terrains</span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/main/terrains" 
                               class="btn btn-sm btn-action me-1">Liste</a>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3 col-6 mb-3">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-calendar-alt feature-icon"></i>
                            <span>Réservations</span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/main/reservations" 
                               class="btn btn-sm btn-action me-1">Liste</a>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3 col-6 mb-3">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-money-bill-wave feature-icon"></i>
                            <span>Paiements</span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/main/paiements" 
                               class="btn btn-sm btn-action me-1">Liste</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Fonctionnalités -->
        <h6 class="section-title">
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
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
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
                            <span>Modifier paiement</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/modifierPaiement" 
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
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
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
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
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
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
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
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
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="feature-item">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="fas fa-trash feature-icon"></i>
                            <span>Supprimer anciennes réservations</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/main/supprimerReservationsAvant" 
                           class="btn btn-sm" style="background-color: #f0f0f0; color: var(--lilas-fonce); border: 1px solid var(--lilas-clair);">
                            Accéder
                        </a>
                    </div>
                </div>
            </div>
        </div>

        

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>