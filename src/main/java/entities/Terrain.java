package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity  
@Table(name = "terrains")
@NamedQueries({
    @NamedQuery(name = "Terrain.findAll", query = "SELECT t FROM Terrain t ORDER BY t.nom"),
    @NamedQuery(name = "Terrain.findById", query = "SELECT t FROM Terrain t WHERE t.idTerrain = :id"),
    @NamedQuery(name = "Terrain.findByType", query = "SELECT t FROM Terrain t WHERE t.type = :type"),
    @NamedQuery(name = "Terrain.findTerrainsNonReserves", 
        query = "SELECT t.nom, t.localisation FROM Terrain t WHERE t.idTerrain NOT IN (SELECT r.terrain.idTerrain FROM Reservation r)"),
    @NamedQuery(name = "Terrain.getReservationsParTerrain", 
        query = "SELECT t.nom, t.idTerrain, COUNT(r) FROM Terrain t LEFT JOIN t.reservations r GROUP BY t.idTerrain, t.nom ORDER BY t.nom")
})
public class Terrain implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_terrain")
    private Long idTerrain;
    
    @Column(name = "nom", length = 50, nullable = false)
    private String nom;
    
    @Column(name = "type", length = 50, nullable = false)
    private String type;
    
    @Column(name = "capacite", nullable = false)
    private Integer capacite;
    
    @Column(name = "localisation", length = 100, nullable = false)
    private String localisation;
    
    @Column(name = "prix_heure", nullable = false)
    private Double prixHeure; // Ajout du champ prix par heure
    
    @OneToMany(mappedBy = "terrain", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reservation> reservations = new ArrayList<>();
    
    // Constructeurs
    public Terrain() {
        this.prixHeure = 25.0; // Valeur par défaut
    }
    
    public Terrain(String nom, String type, Integer capacite, String localisation) {
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prixHeure = 25.0; // Valeur par défaut
    }
    
    public Terrain(String nom, String type, Integer capacite, String localisation, Double prixHeure) {
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.localisation = localisation;
        this.prixHeure = prixHeure;
    }
    
    // Getters et Setters
    public Long getIdTerrain() { 
        return idTerrain; 
    }
    
    public void setIdTerrain(Long idTerrain) { 
        this.idTerrain = idTerrain; 
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getType() { 
        return type; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }
    
    public Integer getCapacite() { 
        return capacite; 
    }
    
    public void setCapacite(Integer capacite) { 
        this.capacite = capacite; 
    }
    
    public String getLocalisation() { 
        return localisation; 
    }
    
    public void setLocalisation(String localisation) { 
        this.localisation = localisation; 
    }
    
    public Double getPrixHeure() { 
        return prixHeure; 
    }
    
    public void setPrixHeure(Double prixHeure) { 
        this.prixHeure = prixHeure; 
    }
    
    public List<Reservation> getReservations() { 
        return reservations; 
    }
    
    public void setReservations(List<Reservation> reservations) { 
        this.reservations = reservations; 
    }
    
    // Méthodes utilitaires
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setTerrain(this);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setTerrain(null);
    }
    
    // Méthode pour obtenir le prix par défaut selon le type de terrain
    public static Double getPrixParDefautParType(String type) {
        if (type == null) return 25.0;
        
        switch (type.toLowerCase()) {
            case "football": return 30.0;
            case "tennis": return 25.0;
            case "basket": return 20.0;
            case "padel": return 35.0;
            case "volley": return 15.0;
            case "badminton": return 18.0;
            case "rugby": return 40.0;
            case "handball": return 22.0;
            default: return 25.0;
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTerrain != null ? idTerrain.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Terrain)) {
            return false;
        }
        Terrain other = (Terrain) object;
        if ((this.idTerrain == null && other.idTerrain != null) || 
            (this.idTerrain != null && !this.idTerrain.equals(other.idTerrain))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Terrain[id=" + idTerrain + ", nom=" + nom + ", type=" + type + ", prix=" + prixHeure + "€/h]";
    }

    public void setSuperficie(double superficie) {
        // Cette méthode semble être un artefact, vous pouvez la supprimer si non utilisée
    }
}