<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter un Client</title>
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
        
        .form-control:focus {
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
                <i class="fas fa-user-plus me-2"></i>Nouveau Client
            </h5>
        </div>

        <!-- Navigation -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/main/clients" class="btn btn-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i> Retour
            </a>
        </div>

        <!-- Formulaire -->
        <form action="${pageContext.request.contextPath}/main/ajouterClient" method="POST" id="clientForm">
            
            <div class="mb-3">
                <label for="nom" class="form-label">
                    Nom complet <span class="required-star">*</span>
                </label>
                <input type="text" class="form-control" id="nom" name="nom" 
                       required placeholder="Ex: Jean Dupont">
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">
                    Email <span class="required-star">*</span>
                </label>
                <input type="email" class="form-control" id="email" name="email" 
                       required placeholder="exemple@email.com">
            </div>

            <div class="mb-3">
                <label for="telephone" class="form-label">
                    Téléphone <span class="required-star">*</span>
                </label>
                <input type="tel" class="form-control" id="telephone" name="telephone" 
                       required placeholder="0612345678">
            </div>

            <div class="mb-4">
                <label for="dateInscription" class="form-label">
                    Date d'inscription
                </label>
                <input type="date" class="form-control" id="dateInscription" 
                       name="dateInscription">
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
            document.getElementById('dateInscription').value = today;
            
            // Validation simple
            document.getElementById('clientForm').addEventListener('submit', function(event) {
                if (!this.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    this.classList.add('was-validated');
                }
            });
        });

        function resetForm() {
            const form = document.getElementById('clientForm');
            form.reset();
            form.classList.remove('was-validated');
            document.getElementById('dateInscription').value = new Date().toISOString().split('T')[0];
        }
    </script>
</body>
</html>