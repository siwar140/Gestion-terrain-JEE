package services;

import entities.Terrain;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ILocalTerrainService {
    // CRUD
    void addTerrain(Terrain terrain);
    void updateTerrain(Terrain terrain);
    void deleteTerrain(Long id);
    Terrain getTerrainById(Long id);
    List<Terrain> getAllTerrains();
    
    // Requêtes spécifiques
    List<Object[]> getTerrainsNonReserves();
    List<Object[]> getReservationsParTerrain();
    
    // Recherche
    List<Terrain> rechercherTerrainsParType(String type);
    List<Terrain> rechercherTerrainsParLocalisation(String localisation);
    
    // Formaté
    List<Object[]> getTerrainsAvecNombreReservationsFormate();
}