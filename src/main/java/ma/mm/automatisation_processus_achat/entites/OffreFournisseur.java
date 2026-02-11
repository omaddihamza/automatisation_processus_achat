package ma.mm.automatisation_processus_achat.entites;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;


@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class OffreFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(columnDefinition = "BYTEA")
    private byte[] offreTechnique;
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(columnDefinition = "BYTEA")
    private byte[] offreCommercial;
    private LocalDateTime creationDate;
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = true)
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "cadre_achat_id", nullable = true)
    private CadreAchat cadreAchat;
}
