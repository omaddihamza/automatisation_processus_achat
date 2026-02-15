/*
package ma.mm.automatisation_processus_achat.entites;

import jakarta.persistence.*;
import lombok.*;
import ma.mm.automatisation_processus_achat.enums.Status;
import ma.mm.automatisation_processus_achat.enums.TypeAchate;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class CadreAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String SpecificationTechnique;
    @Enumerated(EnumType.STRING)
    private TypeAchate typeAchate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime creationDate;
    private LocalDateTime  modificationDate;
    private int meilleurOffre;
    @OneToOne
    private Cps cps;
    @OneToMany(mappedBy = "cadreAchat")
    private List<OffreFournisseur> offres;
}
*/
