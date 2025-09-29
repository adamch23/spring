package ressources.com.ressources.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRessource;

    @JsonProperty("nomRessource")
    private String nomRessource;

    @JsonProperty("nombreRessource")
    private int nombreRessource;

    @JsonProperty("typesRessource")
    private String typesRessource;

    @JsonProperty("cost")
    private int cost;

    @ElementCollection
    @MapKeyColumn(name = "projectId")
    @Column(name = "nombreRessourceAffecte")
    @JsonProperty("idProjets")
    private Map<Integer, Integer> idProjets = new HashMap<>();
}
