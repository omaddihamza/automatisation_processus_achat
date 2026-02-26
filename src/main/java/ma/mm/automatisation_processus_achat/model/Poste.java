package ma.mm.automatisation_processus_achat.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString @Builder
public class Poste {
    @Id
    private Long id;
    private String numero;
    private String designation;
    private String quantite;
    private String unite;
}
