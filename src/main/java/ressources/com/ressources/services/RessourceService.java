package ressources.com.ressources.services;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ressources.com.ressources.entities.Ressource;
import ressources.com.ressources.repositories.RessourceRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RessourceService implements IRessourceService {

    RessourceRepository ressourceRepository;
    private final ProjectClient projectClient; // Injection du client Feign
    private final JavaMailSender mailSender; // Injection de JavaMailSender

    @Override
    public Ressource addRessource(Ressource r) {
        return ressourceRepository.save(r);
    }

    @Override
    public List<Ressource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    @Override
    public Ressource addRessource1(Ressource r) {
        return ressourceRepository.save(r);
    }

    @Override
    public Ressource ajouterRessource(Ressource r) {
        return ressourceRepository.save(r);
    }

    @Override
    public Ressource updateRessource(Long id, Ressource r) {
        Optional<Ressource> existingRessource = ressourceRepository.findById(id);
        if (existingRessource.isPresent()) {
            r.setIdRessource(id);
            return ressourceRepository.save(r);
        }
        throw new RuntimeException("Ressource not found with id: " + id);
    }

    /* @Override
     public void deleteRessource(Long id) {
         if (ressourceRepository.existsById(id)) {
             ressourceRepository.deleteById(id);
         } else {
             throw new RuntimeException("Ressource not found with id: " + id);
         }
     }
 */
    @Override
    public void deleteRessource(Long id) {
        if (ressourceRepository.existsById(id)) {
            try {
                // Supprimer la relation projet-ressource avant de supprimer la ressource
                projectClient.removeRessourceFromProject(id);
                System.out.println("🗑️ Relation projet-ressource supprimée pour la ressource ID : " + id);
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la suppression de la relation projet-ressource : " + e.getMessage());
            }

            // Supprimer la ressource après la suppression de la relation
            ressourceRepository.deleteById(id);
            System.out.println("✅ Ressource supprimée avec succès : " + id);
        } else {
            throw new RuntimeException("❌ Ressource not found with id: " + id);
        }
    }

    @Override
    public Ressource getRessourceById(Long id) {
        return ressourceRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Ressource not found with id: " + id)
        );
    }

    @Override
    public Ressource assignRessourceToProject(Long ressourceId, int projectId, int nombreRessource) {
        // Récupérer la ressource depuis la base de données
        Optional<Ressource> ressourceOpt = ressourceRepository.findById(ressourceId);
        if (!ressourceOpt.isPresent()) {
            throw new RuntimeException("Ressource non trouvée avec id: " + ressourceId);
        }

        // Récupérer l'objet ressource
        Ressource ressource = ressourceOpt.get();

        // Vérifier si la ressource a suffisamment de stock disponible
        if (ressource.getNombreRessource() < nombreRessource) {
            throw new RuntimeException("Pas assez de ressources disponibles pour l'affectation.");
        }

        // Diminuer le nombre de ressources disponibles
        ressource.setNombreRessource(ressource.getNombreRessource() - nombreRessource);

        // Ajouter ou mettre à jour la relation (nombre de ressources affectées) dans le projet
        // On utilise un Map<Integer, Integer> pour associer projectId et nombre de ressources
        Map<Integer, Integer> projets = ressource.getIdProjets();
        if (projets == null) {
            projets = new HashMap<>();
        }
        projets.put(projectId, nombreRessource);

        ressource.setIdProjets(projets);  // Mettez à jour le Map dans l'objet ressource

        // Sauvegarder les modifications de la ressource dans la base de données
        ressourceRepository.save(ressource);

        // Utiliser le client Feign pour notifier l'affectation de la ressource au projet
        // String response = projectClient.assignRessourceToProject(projectId, ressourceId );
        // System.out.println("Réponse du service Project: " + response);

        return ressource;
    }

    // Nouvelle méthode pour envoyer le rapport des ressources
    @Override
    public void envoyerRapportRessources(String destinataire) {
        // Récupérer toutes les ressources depuis la base de données
        List<Ressource> ressources = ressourceRepository.findAll();

        // Vérifier si la liste est vide
        if (ressources.isEmpty()) {
            System.out.println("Aucune ressource disponible pour générer un rapport.");
            return;
        }

        // Construire le contenu de l'email
        StringBuilder contenu = new StringBuilder("📊 **Demandes des Ressources** 📊\n\n");

        for (Ressource res : ressources) {
            String etat;
            if (res.getNombreRessource() == 0) {
                etat = "Épuisée ❌";
            } else if (res.getNombreRessource() <= 50) {
                etat = "Critique ⚠️";
            } else {
                etat = "Disponible ✅";
            }

            contenu.append("🔹 ").append(res.getNomRessource())
                    .append(" : ").append(res.getNombreRessource()).append(" unités (")
                    .append(etat).append(")\n");
        }

        // Ajout de la date d'envoi
        contenu.append("\n📅 Date d'envoi : ").append(java.time.LocalDate.now());

        // Création et envoi de l'e-mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("📢 Rapport des Ressources Disponibles");
        message.setText(contenu.toString());

        mailSender.send(message);
        System.out.println("✅ E-mail envoyé avec succès à " + destinataire);
    }

    ////pour chercher la liste des  ressource affecter a un projet
    @Override
    public List<Ressource> findRessourcesByProjectId(int projectId) {
        System.out.println("🔍 Recherche des ressources pour le projet ID: " + projectId);
        return ressourceRepository.findByIdProjets(projectId);
    }
// afficher le détaille d'une ressource :historique de chaque ressource

    /*
@Override
public Map<Integer, Integer> getProjectsForRessource(Long ressourceId) {
    Optional<Ressource> ressourceOpt = ressourceRepository.findById(ressourceId);

    if (!ressourceOpt.isPresent()) {
        throw new RuntimeException("Ressource non trouvée avec id: " + ressourceId);
    }

    Ressource ressource = ressourceOpt.get();
    return ressource.getIdProjets(); // Retourne la map des projets avec le nombre de ressources affectées
}

*/
    // Méthode pour récupérer les détails du projet
    @Override
    public Map<Integer, Integer> getProjectsForRessource(Long ressourceId) {
        Optional<Ressource> ressourceOpt = ressourceRepository.findById(ressourceId);

        if (!ressourceOpt.isPresent()) {
            throw new RuntimeException("Ressource non trouvée avec id: " + ressourceId);
        }

        Ressource ressource = ressourceOpt.get();
        return ressource.getIdProjets(); // Retourne la map des projets avec le nombre de ressources affectées
    }
}