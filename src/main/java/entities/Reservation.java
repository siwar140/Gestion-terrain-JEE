package entities;

import jakarta.persistence.*; 
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;
    
    @Column(name = "date_reservation", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateReservation;
    
    @Column(name = "heure_debut", nullable = false)
    private String heureDebut;
    
    @Column(name = "heure_fin", nullable = false)
    private String heureFin;
    
    @Column(name = "mode_paiement")
    private String modePaiement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terrain", nullable = false)
    private Terrain terrain;
    
    // IMPORTANT: Relation inverse
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Paiement paiement;
    
    public Reservation() {}
    
    public Reservation(Date dateReservation, String heureDebut, String heureFin, Client client, Terrain terrain) {
        this.dateReservation = dateReservation;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.client = client;
        this.terrain = terrain;
        this.modePaiement = "non payé"; // Par défaut
        this.paiement = null; // Par défaut, pas de paiement
    }
    
    // Getters et Setters
    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }
    
    public Date getDateReservation() { return dateReservation; }
    public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }
    
    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }
    
    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
    
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public Terrain getTerrain() { return terrain; }
    public void setTerrain(Terrain terrain) { this.terrain = terrain; }
    
    public Paiement getPaiement() { return paiement; }
    public void setPaiement(Paiement paiement) { this.paiement = paiement; }

    public boolean isPayee() {
        // Le paiement est considéré comme payé si le modePaiement n'est PAS "non payé"
        return this.modePaiement != null && !"non payé".equals(this.modePaiement);
    }

    public String getStatutPaiement() {
        if (this.modePaiement == null || "non payé".equals(this.modePaiement)) {
            return "Non payé";
        } else {
            return "Payé (" + this.modePaiement + ")";
        }
    }
    public void setDuree(int minutes) {
        if (this.heureDebut != null && minutes > 0) {
            try {
                String[] parts = this.heureDebut.split(":");
                int heures = Integer.parseInt(parts[0]);
                int mins = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                int totalMinutes = heures * 60 + mins + minutes;
                
                int finHeures = totalMinutes / 60;
                int finMins = totalMinutes % 60;
                
                this.heureFin = String.format("%02d:%02d", finHeures, finMins);
            } catch (Exception e) {
                System.err.println("Erreur de format d'heure: " + this.heureDebut);
            }
        }
    }
    
    public void setDuree(long minutes) {
        setDuree((int) minutes);
    }
    
    public int getDuree() {
        if (this.heureDebut != null && this.heureFin != null) {
            try {
                String[] debutParts = this.heureDebut.split(":");
                String[] finParts = this.heureFin.split(":");
                
                int debutHeures = Integer.parseInt(debutParts[0]);
                int debutMinutes = debutParts.length > 1 ? Integer.parseInt(debutParts[1]) : 0;
                
                int finHeures = Integer.parseInt(finParts[0]);
                int finMinutes = finParts.length > 1 ? Integer.parseInt(finParts[1]) : 0;
                
                return (finHeures * 60 + finMinutes) - (debutHeures * 60 + debutMinutes);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Reservation{id=" + idReservation + 
               ", date=" + dateReservation + 
               ", client=" + (client != null ? client.getNom() : "null") +
               ", terrain=" + (terrain != null ? terrain.getNom() : "null") + "}";
    }
}