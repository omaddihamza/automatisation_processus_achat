package ma.mm.automatisation_processus_achat.endpoint;

import ma.mm.automatisation_processus_achat.entites.OffreFournisseur;
import ma.mm.automatisation_processus_achat.service.OffreFournisseurServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class OffreFouisseurConntroller {
    private final OffreFournisseurServiceImpl offreFournisseurService;

    public OffreFouisseurConntroller(OffreFournisseurServiceImpl offreFournisseurService) {
        this.offreFournisseurService = offreFournisseurService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public OffreFournisseur saveOffreFournisseur(@RequestParam("offreTechnique") MultipartFile offreTechnique,
                                                 @RequestParam("offreCommercial") MultipartFile offreCommercial) throws IOException {

        return offreFournisseurService.saveOffreFournisseur(offreTechnique, offreCommercial);
    }
}
