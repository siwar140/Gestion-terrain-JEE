<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter une Réservation</title>
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
            max-width: 500px;
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
        
        .form-label {
            font-weight: 600;
            color: var(--lilas-fonce);
            margin-bottom: 8px;
        }
        
        .form-control:focus, .form-select:focus {
            border-color: var(--lilas);
            box-shadow: 0 0 0 0.2rem rgba(138, 75, 175, 0.25);
        }
        
        .required-star {
            color: #dc3545;
        }
        
        .time-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .time-separator {
            color: var(--lilas-fonce);
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-0">
                <i class="fas fa-calendar-plus me-2"></i>Nouvelle Réservation
            </h5>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/reservations" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Retour
            </a>
        </div>

        <!-- Formulaire -->
        <form action="${pageContext.request.contextPath}/main/ajouterReservation" method="POST" id="reservationForm">
            
            <div class="mb-3">
                <label for="clientId" class="form-label">
                    Client <span class="required-star">*</span>
                </label>
                <select class="form-select" id="clientId" name="clientId" required>
                    <option value="">-- Sélectionnez un client --</option>
                    <c:forEach var="client" items="${clients}">
                        <option value="${client.idClient}">
                            ${client.nom}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label for="terrainId" class="form-label">
                    Terrain <span class="required-star">*</span>
                </label>
                <select class="form-select" id="terrainId" name="terrainId" required>
                    <option value="">-- Sélectionnez un terrain --</option>
                    <c:forEach var="terrain" items="${terrains}">
                        <option value="${terrain.idTerrain}">
                            ${terrain.nom} (${terrain.type})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label for="dateReservation" class="form-label">
                    Date <span class="required-star">*</span>
                </label>
                <input type="date" class="form-control" id="dateReservation" 
                       name="dateReservation" required>
            </div>

            <div class="mb-4">
                <label class="form-label">
                    Horaire <span class="required-star">*</span>
                </label>
                <div class="time-group">
                    <input type="time" class="form-control" id="heureDebut" 
                           name="heureDebut" required>
                    <span class="time-separator">à</span>
                    <input type="time" class="form-control" id="heureFin" 
                           name="heureFin" required>
                </div>
            </div>

            <!-- Boutons -->
            <div class="d-flex justify-content-between mt-4 pt-3 border-top">
                <button type="reset" class="btn btn-secondary" onclick="resetForm()">
                    Réinitialiser
                </button>
                <button type="submit" class="btn btn-submit">
                    Enregistrer
                </button>
            </div>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Date du jour par défaut
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('dateReservation').value = today;
            
            // Heures par défaut (maintenant + 1 heure)
            const now = new Date();
            const heureDebut = new Date(now.getTime() + 60 * 60 * 1000);
            const heureFin = new Date(heureDebut.getTime() + 60 * 60 * 1000);
            
            document.getElementById('heureDebut').value = 
                `${heureDebut.getHours().toString().padStart(2, '0')}:00`;
            document.getElementById('heureFin').value = 
                `${heureFin.getHours().toString().padStart(2, '0')}:00`;
            
            // Validation simple
            document.getElementById('reservationForm').addEventListener('submit', function(event) {
                if (!this.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    this.classList.add('was-validated');
                }
            });
        });

        function resetForm() {
            const form = document.getElementById('reservationForm');
            form.reset();
            form.classList.remove('was-validated');
            
            // Réinitialiser aux valeurs par défaut
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('dateReservation').value = today;
            
            const now = new Date();
            const heureDebut = new Date(now.getTime() + 60 * 60 * 1000);
            const heureFin = new Date(heureDebut.getTime() + 60 * 60 * 1000);
            
            document.getElementById('heureDebut').value = 
                `${heureDebut.getHours().toString().padStart(2, '0')}:00`;
            document.getElementById('heureFin').value = 
                `${heureFin.getHours().toString().padStart(2, '0')}:00`;
        }
    </script>
</body>
</html>