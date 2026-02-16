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
import java.util.stream.Collectors;

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

    /*public Map<String, Object> verifierConformite(String fournisseur) {

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
    }*/

    public Map<String, Object> verifierConformite(String fournisseur) {

        List<Poste> postesCps = cpsService.getPostes();

        // récupérer uniquement la première page
        SearchRequest request = SearchRequest.builder()
                .query("designation produit")
                .topK(20)
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);

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


        // 3️⃣ Transformer en List<Map> manuellement
        // Supposons que le LLM renvoie toujours ce format simple
        List<Map<String, String>> postesList = new ArrayList<>();

        // Split sur les objets JSON individuels
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

        // 4️⃣ Créer la réponse finale
        Map<String, Object> response = new HashMap<>();
        response.put("fournisseur", fournisseur);
        response.put("postes", postesList);

        return response;


//        return Map.of("result", response);
    }

   /* public Map<String, Object> verifierConformite(String fournisseur) {

        List<Poste> postesCps = cpsService.getPostes(); // postes du CPS

        // 🔹 Récupérer toutes les premières pages du fournisseur
        SearchRequest request = SearchRequest.builder()
                .query("") // vide pour récupérer tous les documents
                .topK(100) // prend jusqu'à 100 documents, ou plus selon ton besoin
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);

        // filtrer uniquement les premières pages du fournisseur
        List<String> firstPages = docs.stream()
                .filter(doc -> fournisseur.equals(doc.getMetadata().get("fournisseur"))
                        && "OFFER_FIRST_PAGE".equals(doc.getMetadata().get("type")))
                .map(Document::getText)
                .collect(Collectors.toList());

        List<String> postesManquants = new ArrayList<>();

        // 🔹 Comparer chaque poste CPS avec les premières pages
        for (Poste poste : postesCps) {
            boolean trouve = false;

            for (String page : firstPages) {
                // Utiliser un embedding ou similarity simple
                SearchRequest searchRequest = SearchRequest.builder()
                        .query(poste.getDesignation())
                        .topK(1)
                        .build();

                List<Document> result = vectorStore.similaritySearch(searchRequest);

                // Vérifier si la page du fournisseur correspond au poste
                for (Document doc : result) {
                    if (doc.getText().equals(page)) { // ou comparer le score si disponible
                        trouve = true;
                        break;
                    }
                }

                if (trouve) break;
            }

            if (!trouve) {
                postesManquants.add(poste.getDesignation());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fournisseur", fournisseur);
        response.put("conforme", postesManquants.isEmpty());
        response.put("postesManquants", postesManquants);

        return response;
    }*/

}

