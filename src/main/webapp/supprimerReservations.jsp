<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supprimer Anciennes Réservations</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { 
            background-color: #f8f9fa;
            padding: 20px;
        }
        
        .container {
            max-width: 500px;
            margin: 50px auto;
            background: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .alert-box {
            border-left: 4px solid #dc3545;
            background-color: #fff5f5;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .btn-danger {
            width: 100%;
            padding: 10px;
        }
        
        .btn-secondary {
            width: 100%;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Titre -->
        <h4 class="text-center text-danger mb-4">
            Supprimer Anciennes Réservations
        </h4>

        <!-- Message système -->
        <c:if test="${not empty message}">
            <div class="alert alert-${typeMessage == 'success' ? 'success' : 'danger'}">
                ${message}
            </div>
        </c:if>

        <!-- Avertissement -->
        <div class="alert-box mb-4">
            <p class="mb-1"><strong>Avertissement :</strong></p>
            <p class="small mb-0">
                Cette action supprimera définitivement toutes les réservations antérieures à l'année spécifiée.
                Les paiements associés seront également supprimés.
            </p>
        </div>

        <!-- Formulaire -->
        <form action="${pageContext.request.contextPath}/main/executerSuppression" method="POST">
            
            <!-- Champ année -->
            <div class="mb-3">
                <label class="form-label">Année limite :</label>
                <input type="number" class="form-control" name="annee" 
                       id="annee"
                       placeholder="Ex: 2022" 
                       min="2000" max="2025" 
                       required>
                <div class="form-text text-muted">
                    Toutes les réservations avant le 1er janvier <span id="yearDisplay">____</span> seront supprimées.
                </div>
            </div>

            <!-- Confirmation -->
            <div class="mb-4">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="confirmation" required>
                    <label class="form-check-label" for="confirmation">
                        Je confirme comprendre que cette action est irréversible
                    </label>
                </div>
            </div>

            <!-- Boutons -->
            <div>
                <button type="submit" class="btn btn-danger" id="submitBtn">
                    Supprimer les réservations
                </button>
                <a href="${pageContext.request.contextPath}/main/" class="btn btn-secondary">
                    Annuler
                </a>
            </div>
        </form>
    </div>

    <script>
        // Configuration de l'année par défaut
        document.addEventListener('DOMContentLoaded', function() {
            const currentYear = new Date().getFullYear();
            const yearInput = document.getElementById('annee');
            const yearDisplay = document.getElementById('yearDisplay');
            
            // Définir l'année précédente par défaut
            yearInput.value = currentYear - 1;
            yearDisplay.textContent = yearInput.value;
            
            // Mettre à jour l'affichage
            yearInput.addEventListener('input', function() {
                yearDisplay.textContent = this.value;
            });
        });
        
        // Confirmation avant suppression
        document.querySelector('form').addEventListener('submit', function(e) {
            const year = document.querySelector('input[name="annee"]').value;
            const confirmed = confirm(
                'Confirmez-vous la suppression définitive de toutes les réservations avant ' + year + ' ?\n\n' +
                'Cette action est irréversible.'
            );
            
            if (!confirmed) {
                e.preventDefault();
            }
        });
    </script>
</body>
</html>