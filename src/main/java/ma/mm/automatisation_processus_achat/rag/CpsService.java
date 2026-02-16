package ma.mm.automatisation_processus_achat.rag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CpsService {

    @Getter
    private List<Poste> postes = new ArrayList<>();
    private final SimpleVectorStore cpsVectorStore;
    private ObjectMapper objectMapper; // Jackson

    private final String fileStore = "storeCPS.json";
    private ChatClient chatClient;

    public CpsService(SimpleVectorStore cpsVectorStore, ChatClient.Builder chatClient,
                      ObjectMapper objectMapper) {
        this.cpsVectorStore = cpsVectorStore;
        this.chatClient = chatClient.build();
        this.objectMapper = objectMapper;
    }

    public String loadFileCPS(MultipartFile pdfFile) throws JsonProcessingException {
        Path path = Path.of("src", "main", "resources", "store");
        File file = new File(path.toFile(), fileStore);
        if (file.exists()) {
            cpsVectorStore.load(file);
        }
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
        //pour retourner une liste de document(chunks)
        List<Document> documents = pdfDocumentReader.get();
        //decouper les pages en chunks
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks = textSplitter.apply(documents);
        //envoyer les chunks vers simple vector store
        cpsVectorStore.add(chunks);
        cpsVectorStore.save(file);
        //extraire les postes du fichier CPS
        SearchRequest request = SearchRequest.builder()
                .query("bordereau des prix ou liste des postes ou designation")
                .topK(20)
                .build();

        List<Document> cpsDocs = cpsVectorStore.similaritySearch(request);

        String cpsText = cpsDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));


        String prompt = """
        Voici un extrait du CPS contenant un bordereau des prix.
        
        Extrais uniquement la liste des postes sous format JSON.
        Les postes se trouvent dans le bordereau.
        Si le poste a une designation abreviee cherche le nom complet dans le cps et adapte le nom complet pour le poste
        Chaque poste doit contenir :
        - numero
        - designation
        - quantite
        - unite 
        
        Ne retourne que du JSON valide.
        
        Texte :
        """ + cpsText;

        String jsonResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        jsonResponse = jsonResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();
        //  Convertir JSON → List<Poste>
        List<Poste> postes = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {
                }
        );

        //  Sauvegarder en mémoire
        savePostes(postes);
        return jsonResponse;
    }

    public void savePostes(List<Poste> postesExtraits) {
        this.postes = postesExtraits;
    }


}

