package ressources.com.ressources.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;


//@FeignClient(name = "project", url = "http://localhost:8085/project")
@FeignClient(name = "PROJECT")
public interface ProjectClient {
    @PostMapping("/project/projects/{projectId}/assign/{ressourceId}")
    String assignRessourceToProject(@PathVariable("projectId") int projectId,
                                    @PathVariable("ressourceId") Long ressourceId);

    // Nouvelle méthode pour supprimer la relation entre une ressource et un projet
    @DeleteMapping("/project/projects/deleteRessource/{id}")
    void removeRessourceFromProject(@PathVariable("id") Long id);
    // Méthode pour récupérer les détails du projet (Sans DTO)
    // ✅ Récupère les détails d'un projet sous forme de Map

    // Méthode pour récupérer les détails du projet
    @GetMapping("/project/projects/{projectId}")
    Map<String, Object> getProjectDetails(@PathVariable("projectId") int projectId);
}
