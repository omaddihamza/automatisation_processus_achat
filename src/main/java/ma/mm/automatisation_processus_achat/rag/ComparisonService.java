package ma.mm.automatisation_processus_achat.rag;

import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    private SimpleVectorStore fournisseurVectorStore;
    private final ChatClient chatClient;
    private final CpsService cpsService;


    public ComparisonService(
            @Qualifier("fournisseurStore")
            SimpleVectorStore fournisseurVectorStore,
                             ChatClient.Builder builder, CpsService cpsService) {
        this.fournisseurVectorStore = fournisseurVectorStore;
        this.chatClient = builder.build();
        this.cpsService = cpsService;
    }

    public Map<String, Object> verifierConformite(String fournisseur) {

        List<Poste> postesCps = cpsService.getPostes();

        // récupérer uniquement la première page
        SearchRequest request = SearchRequest.builder()
                .query("designation produit")
                .topK(20)
                .build();

        List<Document> docs = fournisseurVectorStore.similaritySearch(request);

        String firstPageText = docs.stream()
                .filter(doc ->
                        fournisseur.equals(doc.getMetadata().get("fournisseur")) &&
                                "OFFER_FIRST_PAGE".equals(doc.getMetadata().get("type"))
                )
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String prompt = """
    Voici les postes du CPS :
    """ + postesCps + """

    Voici la première page de l'offre fournisseur :
    """ + firstPageText + """

    La premiere page de l'offre fournisseur contient la marque qui correspond a chaque poste.
    Si la premiere page de l'offre fournisseur contient un lien ne le lit pas tu l'ignore.
    Pour chaque poste du CPS, indique :
    - CONFORME si la marque dans l'offre fournisseur est adequate au poste
    - NON CONFORME

    Ne mets pas ```json.
    Ne mets pas de markdown.
    Retourne uniquement du JSON brut.

//    Retourne uniquement un JSON.
    """;

        String responseLLM = chatClient.prompt()
                .user(prompt)
                .call()
                .content();


        //  Transformer en List<Map> manuellement
        List<Map<String, String>> postesList = new ArrayList<>();

        String[] items = responseLLM.replace("[", "").replace("]", "").split("\\},\\s*\\{");
        for (String item : items) {
            item = item.replace("{", "").replace("}", "").trim();
            Map<String, String> map = new HashMap<>();
            String[] fields = item.split(",");
            for (String field : fields) {
                String[] keyValue = field.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    map.put(key, value);
                }
            }
            postesList.add(map);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fournisseur", fournisseur);
        response.put("postes", postesList);

        return response;
    }

}

