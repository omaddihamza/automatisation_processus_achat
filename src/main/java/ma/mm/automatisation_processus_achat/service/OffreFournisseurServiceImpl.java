/*
package ma.mm.automatisation_processus_achat.service;

import ma.mm.automatisation_processus_achat.entites.OffreFournisseur;
import ma.mm.automatisation_processus_achat.repository.CadreAchatRepository;
import ma.mm.automatisation_processus_achat.repository.FournisseurRepository;
import ma.mm.automatisation_processus_achat.repository.OffreFournisseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class OffreFournisseurServiceImpl implements IOffreFournisseurService {

    private final OffreFournisseurRepository offreFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CadreAchatRepository cadreAchatRepository;


    public OffreFournisseurServiceImpl(OffreFournisseurRepository offreFournisseurRepository, FournisseurRepository fournisseurRepository, CadreAchatRepository cadreAchatRepository) {
        this.offreFournisseurRepository = offreFournisseurRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.cadreAchatRepository = cadreAchatRepository;
    }

    @Override
    public OffreFournisseur saveOffreFournisseur(MultipartFile offreTechnique, MultipartFile offreCommercial) throws IOException {
        OffreFournisseur offreFournisseur = OffreFournisseur.builder()
                .offreTechnique(offreTechnique.getBytes())
                .offreCommercial(offreCommercial.getBytes())
                .creationDate(LocalDateTime.now())
                .build();
        return offreFournisseurRepository.save(offreFournisseur);
    }
}
*/
