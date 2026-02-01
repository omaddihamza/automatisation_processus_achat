package ma.mm.automatisation_processus_achat.model;

import java.time.LocalDate;

/**
 * @author Siham
 **/
    public record CpsData(
            String revisionDesPrix,
            String specificationTechniques,
            String lieuLivraisonFournitures,
            String BordereauxDesPrix
    ) {}

