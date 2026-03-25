package entities;

import jakarta.persistence.*; 
import java.util.Date;

@Entity
@Table(name = "paiements")
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long idPaiement;
    
    @Column(name = "montant", nullable = false)
    private Double montant;
    
    @Column(name = "date_paiement")
    @Temporal(TemporalType.DATE)
    private Date datePaiement;
    
    @Column(name = "mode_paiement")
    private String modePaiement;
    
    // IMPORTANT: Relations bidirectionnelles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", unique = true, nullable = false)
    private Reservation reservation;
    
    // Constructeurs
    public Paiement() {}
    
    public Paiement(Double montant, Date datePaiement, String modePaiement, Reservation reservation, Client client) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.modePaiement = modePaiement;
        this.reservation = reservation;
        this.client = client;
    }
    
    // Getters et Setters
    public Long getIdPaiement() { 
        return idPaiement; 
    }
    
    public void setIdPaiement(Long idPaiement) { 
        this.idPaiement = idPaiement; 
    }
    
    public Double getMontant() { 
        return montant; 
    }
    
    public void setMontant(Double montant) { 
        this.montant = montant; 
    }
    
    public Date getDatePaiement() { 
        return datePaiement; 
    }
    
    public void setDatePaiement(Date datePaiement) { 
        this.datePaiement = datePaiement; 
    }
    
    public String getModePaiement() { 
        return modePaiement; 
    }
    
    public void setModePaiement(String modePaiement) { 
        this.modePaiement = modePaiement; 
    }
    
    public Reservation getReservation() { 
        return reservation; 
    }
    
    public void setReservation(Reservation reservation) { 
        this.reservation = reservation; 
    }
    
    public Client getClient() { 
        return client; 
    }
    
    public void setClient(Client client) { 
        this.client = client; 
    }
    
    // Méthode toString
    @Override
    public String toString() {
        return "Paiement{" +
                "idPaiement=" + idPaiement +
                ", montant=" + montant +
                ", datePaiement=" + datePaiement +
                ", modePaiement='" + modePaiement + '\'' +
                ", reservationId=" + (reservation != null ? reservation.getIdReservation() : "null") +
                ", clientId=" + (client != null ? client.getIdClient() : "null") +
                '}';
    }
}