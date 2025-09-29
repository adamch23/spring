package ressources.com.ressources.repositories;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ressources.com.ressources.entities.Ressource;

import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource,Long> {
    /*
    @Query("SELECT r FROM Ressource r WHERE :idProjet MEMBER OF KEY(r.idProjets)")
List<Ressource> findByIdProjets(@Param("idProjet") Integer idProjet);

     */
    @Query(value = "SELECT r.* FROM ressource r " +
            "JOIN ressource_id_projets rip ON r.id_ressource = rip.ressource_id_ressource " +
            "WHERE rip.project_id = :idProjet", nativeQuery = true)
    List<Ressource> findByIdProjets(@Param("idProjet") Integer idProjet);

}
