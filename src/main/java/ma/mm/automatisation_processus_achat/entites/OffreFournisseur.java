package ma.mm.automatisation_processus_achat.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class OffreFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] offreTechnique;
    @Lob
    private byte[] offreCommercial;
    private LocalDateTime creationDate;
    @ManyToOne
    private Fournisseur fournisseur;

    @ManyToOne
    private CadreAchat cadreAchat;
}
