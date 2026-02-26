/*
package ma.mm.automatisation_processus_achat.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomPrenom;
    private Long rcs;
    @OneToMany(mappedBy = "fournisseur")
    private List<OffreFournisseur> offres;
    @OneToOne
    private Contrat contrat;
}
*/
