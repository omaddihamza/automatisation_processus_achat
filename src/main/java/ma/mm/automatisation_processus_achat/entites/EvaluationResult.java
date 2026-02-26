/*
package ma.mm.automatisation_processus_achat.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class EvaluationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean confirmed;
    private int scoreTechnique;
    private int scoreFinancier;
    private int scoreGlobal;
    private String Justification;
    @ManyToOne
    private Fournisseur fournisseur;
    @ManyToOne
    private CadreAchat cadreAchat;
}
*/
