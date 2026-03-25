package entities;

import jakarta.persistence.*; 
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "clients")
@NamedQueries({
    @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c ORDER BY c.nom"),
    @NamedQuery(name = "Client.getClientsQuiReserventFootball", 
                query = "SELECT c.nom, c.email FROM Client c JOIN c.reservations r JOIN r.terrain t WHERE t.type = 'Football'"),
    @NamedQuery(name = "Client.getMontantTotalParClient", 
                query = "SELECT c.nom, SUM(p.montant) FROM Client c JOIN c.paiements p GROUP BY c.nom ORDER BY SUM(p.montant) DESC"),
    @NamedQuery(name = "Client.deleteById", 
                query = "DELETE FROM Client c WHERE c.idClient = :idClient")
})
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long idClient;
    
    @Column(name = "nom", length = 50, nullable = false)
    private String nom;
    
    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;
    
    @Column(name = "telephone", length = 8, nullable = false)
    private String telephone;
    
    @Column(name = "date_inscription", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateInscription;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Paiement> paiements = new ArrayList<>();
    
    // Constructeurs
    public Client() {}
    
    public Client(String nom, String email, String telephone, Date dateInscription) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
    }
    
    // Getters et Setters
    public Long getIdClient() { 
        return idClient; 
    }
    
    public void setIdClient(Long idClient) { 
        this.idClient = idClient; 
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getTelephone() { 
        return telephone; 
    }
    
    public void setTelephone(String telephone) { 
        this.telephone = telephone; 
    }
    
    public Date getDateInscription() { 
        return dateInscription; 
    }
    
    public void setDateInscription(Date dateInscription) { 
        this.dateInscription = dateInscription; 
    }
    
    public List<Reservation> getReservations() { 
        return reservations; 
    }
    
    public void setReservations(List<Reservation> reservations) { 
        this.reservations = reservations; 
    }
    
    public List<Paiement> getPaiements() { 
        return paiements; 
    }
    
    public void setPaiements(List<Paiement> paiements) { 
        this.paiements = paiements; 
    }
    
    // Méthodes utilitaires
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setClient(this);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setClient(null);
    }
    
    public void addPaiement(Paiement paiement) {
        paiements.add(paiement);
        paiement.setClient(this);
    }
    
    public void removePaiement(Paiement paiement) {
        paiements.remove(paiement);
        paiement.setClient(null);
    }
    
    // Méthode toString pour le débogage
    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
    
    // Méthodes equals et hashCode basées sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Client client = (Client) o;
        
        return idClient != null ? idClient.equals(client.idClient) : client.idClient == null;
    }
    
    @Override
    public int hashCode() {
        return idClient != null ? idClient.hashCode() : 0;
    }
}