package ma.mm.automatisation_processus_achat.entitises;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ma.mm.automatisation_processus_achat.Statuts;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
public class CadreAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    @Column( nullable = false, unique=true)
    private String docId;
    private String title;
    private LocalDate publicationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statuts statuts;
    @Enumerated(EnumType.STRING)
    private String typeCadreAchat;

}
