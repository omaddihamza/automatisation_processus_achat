package ma.mm.automatisation_processus_achat.rag;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.IOException;
import java.util.Map;
@Service
public class FournisseurService {

    private final DocumentIndexor indexor;
    private final ComparisonService comparisonService;

    public FournisseurService(DocumentIndexor indexor,
                              ComparisonService comparisonService) {
        this.indexor = indexor;
        this.comparisonService = comparisonService;
    }

    public Map<String, Object> processFournisseur(
            MultipartFile[] files,
            String fournisseur) throws IOException {

        // 1️⃣ Indexation
        indexor.loadFilesFournisseur(files, "OFFER", fournisseur);

        // 2️⃣ Comparaison
        return comparisonService.verifierConformite(fournisseur);
    }
}
