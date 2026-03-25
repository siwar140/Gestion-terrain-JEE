<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter un Terrain</title>
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
    </style>
</head>
<body>
    <div class="container">
        <!-- En-tête -->
        <div class="form-header">
            <h5 class="mb-0">
                <i class="fas fa-plus-circle me-2"></i>Nouveau Terrain
            </h5>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/terrains" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Retour
            </a>
        </div>

        <!-- Formulaire -->
        <form action="${pageContext.request.contextPath}/main/ajouterTerrain" method="POST" id="terrainForm">
            
            <div class="mb-3">
                <label for="nom" class="form-label">
                    Nom du terrain <span class="required-star">*</span>
                </label>
                <input type="text" class="form-control" id="nom" name="nom" 
                       required placeholder="Ex: Terrain A">
            </div>

            <div class="mb-3">
                <label for="type" class="form-label">
                    Type de sport <span class="required-star">*</span>
                </label>
                <select class="form-select" id="type" name="type" required>
                    <option value="">-- Sélectionnez un type --</option>
                    <option value="Football">Football</option>
                    <option value="Tennis">Tennis</option>
                    <option value="Basketball">Basketball</option>
                    <option value="Volleyball">Volleyball</option>
                    <option value="Padel">Padel</option>
                    <option value="Badminton">Badminton</option>
                    <option value="Rugby">Rugby</option>
                    <option value="Handball">Handball</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="localisation" class="form-label">
                    Localisation <span class="required-star">*</span>
                </label>
                <input type="text" class="form-control" id="localisation" name="localisation" 
                       required placeholder="Ex: Zone Nord">
            </div>

            <div class="mb-3">
                <label for="capacite" class="form-label">
                    Capacité (joueurs) <span class="required-star">*</span>
                </label>
                <input type="number" class="form-control" id="capacite" name="capacite" 
                       required min="1" max="1000" placeholder="20">
            </div>

            <div class="mb-4">
                <label for="prixHeure" class="form-label">
                    Prix par heure (€) <span class="required-star">*</span>
                </label>
                <input type="number" class="form-control" id="prixHeure" name="prixHeure" 
                       required min="0" step="0.01" placeholder="25.00">
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
            // Définir des prix par défaut selon le type de sport
            const prixParType = {
                'Football': 30,
                'Tennis': 25,
                'Basketball': 20,
                'Volleyball': 15,
                'Padel': 35,
                'Badminton': 18,
                'Rugby': 40,
                'Handball': 22
            };

            const capaciteParType = {
                'Football': 22,
                'Tennis': 4,
                'Basketball': 10,
                'Volleyball': 12,
                'Padel': 4,
                'Badminton': 4,
                'Rugby': 30,
                'Handball': 14
            };

            // Mettre à jour automatiquement le prix et la capacité selon le type sélectionné
            document.getElementById('type').addEventListener('change', function() {
                const type = this.value;
                const prixInput = document.getElementById('prixHeure');
                const capaciteInput = document.getElementById('capacite');
                
                if (prixParType[type]) {
                    prixInput.value = prixParType[type];
                }
                
                if (capaciteParType[type]) {
                    capaciteInput.value = capaciteParType[type];
                }
            });
            
            // Validation simple
            document.getElementById('terrainForm').addEventListener('submit', function(event) {
                if (!this.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    this.classList.add('was-validated');
                }
            });
        });

        function resetForm() {
            const form = document.getElementById('terrainForm');
            form.reset();
            form.classList.remove('was-validated');
        }
    </script>
</body>
</html>