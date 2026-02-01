/**
 * @author Siham
 **/



package ma.mm.automatisation_processus_achat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.mm.automatisation_processus_achat.model.CpsData;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CpsExtractionService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public CpsExtractionService(ChatClient.Builder chatClientBuilder,
                                VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public CpsData extractCpsInfo(Resource pdfResource) throws JsonProcessingException {

        // 1. Lecture du PDF
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
        List<Document> documents = reader.get();

        // 2. Découpage en chunks
        TokenTextSplitter splitter =
                new TokenTextSplitter(800, 100, 5, 100, true);
        List<Document> chunks = splitter.apply(documents);

        // 3. Embeddings
        vectorStore.add(chunks);

        // 4. Recherche sémantique
        List<Document> relevantChunks = vectorStore.similaritySearch(
                SearchRequest.query("""
                        REVISION DES PRIX,
                        SPECIFICATIONS TECHNIQUES,
                        LIEU DE LIVRAISON DES FOURNITURES,
                        BORDEREAU DES PRIX
                        """).withTopK(5)
        );

        String context = relevantChunks.stream()
                .map(Document::getContent)
                    .collect(Collectors.joining("\n---\n"));

        // 5. Extraction structurée
        String response = chatClient.prompt()
                .user(u -> u.text("""
                Tu es un expert en marchés publics.
                Voici des extraits du CPS :

                {context}

                Réponds UNIQUEMENT avec un JSON valide :

                {
                  "revisionDesPrix": "...",
                  "specificationTechniques": "...",
                  "lieuLivraisonFournitures",
                  "BordereauxDesPrix": "...",            
                }
                """).param("context", context))
                .call()
                .content();

        ObjectMapper mapper = new ObjectMapper();
        CpsData cpsData = mapper.readValue(response, CpsData.class);

        return cpsData;

    }
}

