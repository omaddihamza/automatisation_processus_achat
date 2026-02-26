package ma.mm.automatisation_processus_achat.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
@Service
public class FournisseurService {
    private String fileStoreFournisseur = "storeFournisseur.json";
    private final DocumentIndexor indexor;
    private final ComparisonService comparisonService;
    private SimpleVectorStore fournisseurVectorStore;

    public FournisseurService(DocumentIndexor indexor,
                              ComparisonService comparisonService,
                              @Qualifier("fournisseurStore")
                              SimpleVectorStore fournisseurVectorStore) {
                            this.indexor = indexor;
                            this.comparisonService = comparisonService;
                            this.fournisseurVectorStore = fournisseurVectorStore;
    }

   public Map<String, Object> processFournisseur(
            MultipartFile[] files,
            String fournisseur) {

        // 1️ Indexation
        loadFilesFournisseur(files, "OFFER", fournisseur);

        // 2 Comparaison
        return comparisonService.verifierConformite(fournisseur);
    }

    public void loadFilesFournisseur(MultipartFile[] pdfFiles, String type,
                                     String fournisseur) {

        Path path = Path.of("src", "main", "resources", "storeFournisseur");
        File file = new File(path.toFile(), fileStoreFournisseur);
        if (file.exists()) {
            fournisseurVectorStore.load(file);
        }

        for (MultipartFile pdfFile : pdfFiles) {

            // Lire le PDF et transformer en documents
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
            List<Document> pages = pdfDocumentReader.get();
            if (!pages.isEmpty()) {
                //  première page uniquement
                String firstPageText = pages.get(0).getText();

                //stocker seulement cette page
                Document firstPageDoc = new Document(firstPageText);
                firstPageDoc.getMetadata().put("fournisseur", fournisseur);
                firstPageDoc.getMetadata().put("type", "OFFER_FIRST_PAGE");

                fournisseurVectorStore.add(List.of(firstPageDoc));

            }

        }
        // Sauvegarder le vector store sur fichier
        fournisseurVectorStore.save(file);
    }


}
