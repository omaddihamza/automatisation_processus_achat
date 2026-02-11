package ma.mm.automatisation_processus_achat.service;

import ma.mm.automatisation_processus_achat.entites.OffreFournisseur;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IOffreFournisseurService {

    OffreFournisseur saveOffreFournisseur(MultipartFile offreTechnique, MultipartFile offreCommercial) throws IOException;
}
