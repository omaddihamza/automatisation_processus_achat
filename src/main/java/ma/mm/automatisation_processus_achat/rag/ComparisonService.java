package ma.mm.automatisation_processus_achat.rag;

import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComparisonService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final CpsService cpsService;

    public ComparisonService(VectorStore vectorStore,
                             ChatClient.Builder builder, CpsService cpsService) {
        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
        this.cpsService = cpsService;
    }

    public Map<String, Object> verifierConformite(String fournisseur) {

        List<Poste> postesCps = cpsService.getPostes(); // déjà extraits

        List<String> postesManquants = new ArrayList<>();

        for (Poste poste : postesCps) {

            SearchRequest request = SearchRequest.builder()
                    .query(poste.getDesignation())
                    .topK(5)
                    .build();

            List<Document> results = vectorStore.similaritySearch(request);

            //  Filtrer uniquement documents du fournisseur
            boolean found = results.stream()
                    .anyMatch(doc ->
                            fournisseur.equals(doc.getMetadata().get("fournisseur"))
                                    && "OFFER".equals(doc.getMetadata().get("type"))
                    );

            if (!found) {
                postesManquants.add(poste.getDesignation());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fournisseur", fournisseur);
        response.put("conforme", postesManquants.isEmpty());
        response.put("postesManquants", postesManquants);

        return response;
    }

}

