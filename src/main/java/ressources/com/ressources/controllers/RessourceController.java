package ressources.com.ressources.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import ressources.com.ressources.entities.Ressource;
import ressources.com.ressources.services.IRessourceService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ressources")
@Tag(name = "Ressource Controller", description = "Gestion des ressources")
@CrossOrigin(origins = "*")
public class RessourceController {

    private final IRessourceService ressourceService;
    private final JavaMailSender mailSender; // Injection de JavaMailSender

    @Operation(summary = "Ajouter une ressource")
    @PostMapping("/add")
    public ResponseEntity<Ressource> addRessource(@RequestBody Ressource r) {
        Ressource addedRessource = ressourceService.addRessource1(r);
        return ResponseEntity.ok(addedRessource);
    }

    @Operation(summary = "Lister toutes les ressources")
    @GetMapping("/getAllRessources")
    public ResponseEntity<List<Ressource>> getAllRessources() {
        List<Ressource> ressources = ressourceService.getAllRessources();
        return ResponseEntity.ok(ressources);
    }

    @Operation(summary = "Mettre à jour une ressource")
    @PutMapping("/update/{id}")
    public ResponseEntity<Ressource> updateRessource(@PathVariable Long id, @RequestBody Ressource r) {
        Ressource updatedRessource = ressourceService.updateRessource(id, r);
        return ResponseEntity.ok(updatedRessource);
    }

    @Operation(summary = "Supprimer une ressource")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRessource(@PathVariable Long id) {
        ressourceService.deleteRessource(id);
        return ResponseEntity.ok("Ressource supprimée avec succès!");
    }

    @Operation(summary = "Récupérer une ressource par ID")
    @GetMapping("/getRessourceby/{id}")
    public ResponseEntity<Ressource> getRessourceById(@PathVariable Long id) {
        return ResponseEntity.ok(ressourceService.getRessourceById(id));
    }

   @Operation(summary = "Affecter une ressource à un projet",
            description = "Cette opération affecte une ressource existante à un projet spécifique.")
    @PostMapping("/affecter/{ressourceId}/{projectId}/{nombreRessource}")
    public ResponseEntity<String> affecterRessourceAProject(
            @PathVariable Long ressourceId,
            @PathVariable int projectId, @PathVariable int nombreRessource) {
        Ressource ressource = ressourceService.assignRessourceToProject(ressourceId, projectId, nombreRessource);
        return ResponseEntity.ok("Ressource affectée avec succès !");
    }
    @Operation(summary = "Voir les ressources affectées à un projet avec le nombre de ressources assignées")
    @GetMapping("/getAssignedResources/{projectId}")
    public ResponseEntity<Map<String, Object>> getAssignedResources(@PathVariable int projectId) {
        List<Ressource> ressources = ressourceService.findRessourcesByProjectId(projectId);
        int count = ressources.size();

        Map<String, Object> response = new HashMap<>();
        response.put("resources", ressources);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Envoyer un e-mail avec le résumé des ressources")
    @PostMapping("/envoyerRapport/{destinataire}")
    public ResponseEntity<String> envoyerRapport(@PathVariable String destinataire) {
        ressourceService.envoyerRapportRessources(destinataire);
        return ResponseEntity.ok("📩 Rapport envoyé avec succès à " + destinataire);
    }


    @Operation(summary = "Récupérer les ressources par ID de projet")
    @GetMapping("/getByProjet/{projectId}")
    public List<Ressource> getRessourcesByProject(@PathVariable int projectId) {
        return ressourceService.findRessourcesByProjectId(projectId);
    }

///
@Operation(summary = "Récupérer les projets affectés à une ressource")

@GetMapping("/{id}/projects")
public ResponseEntity<Map<Integer, Integer>> getProjectsForRessource(@PathVariable Long id) {
    return ResponseEntity.ok(ressourceService.getProjectsForRessource(id));
}


}
