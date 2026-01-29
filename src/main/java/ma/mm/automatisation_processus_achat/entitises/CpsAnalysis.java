package ma.mm.automatisation_processus_achat.entitises;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
// this entity to storage the result
public class CpsAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String docId;

    @Column(columnDefinition = "jsonb")
    private String requirementJson;

    private LocalDateTime analysisDate;
}
