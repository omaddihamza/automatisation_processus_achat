package ma.mm.automatisation_processus_achat.rag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Siham
 **/
@Component
public class DocumentIndexor {


    @Value("store.json")
    private String fileStore;
    @Value("storeFournisseur.json")
    private String fileStoreFournisseur;
    private SimpleVectorStore vectorStore;
    private ObjectMapper objectMapper; // Jackson
    private CpsService cpsService;
    private ChatClient chatClient;

    public DocumentIndexor(
            SimpleVectorStore vectorStore , ChatClient.Builder ChatClient, ObjectMapper objectMapper,
            CpsService cpsService) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.build();
        this.objectMapper = objectMapper;
        this.cpsService = cpsService;
    }

    public void loadFile(MultipartFile pdfFile) {
        Path path = Path.of("src", "main", "resources", "store");
        File file = new File(path.toFile(), fileStore);
        if (file.exists()) {
            vectorStore.load(file);
        }
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
        //pour retourner une liste de document(chunks)
        List<Document> documents = pdfDocumentReader.get();
        //decouper les pages en chunks
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks = textSplitter.apply(documents);
        //envoyer les chunks vers simple vector store
        vectorStore.add(chunks);
        vectorStore.save(file);
        //extraire les postes du fichier CPS
        SearchRequest request = SearchRequest.builder()
                .query("bordereau des prix ou liste des postes ou designation")
                .topK(20)
                .build();
    }
//load file cps
public String loadFileCPS(MultipartFile pdfFile) throws JsonProcessingException {
    Path path = Path.of("src", "main", "resources", "store");
    File file = new File(path.toFile(), fileStore);
    if (file.exists()) {
        vectorStore.load(file);
    }
    PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
    //pour retourner une liste de document(chunks)
    List<Document> documents = pdfDocumentReader.get();
    //decouper les pages en chunks
    TextSplitter textSplitter = new TokenTextSplitter();
    List<Document> chunks = textSplitter.apply(documents);
    //envoyer les chunks vers simple vector store
    vectorStore.add(chunks);
    vectorStore.save(file);
    //extraire les postes du fichier CPS
    SearchRequest request = SearchRequest.builder()
            .query("bordereau des prix ou liste des postes ou designation")
            .topK(20)
            .build();

    List<Document> cpsDocs = vectorStore.similaritySearch(request);

    String cpsText = cpsDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n"));

    String prompt = """
        Voici un extrait du CPS contenant un bordereau des prix.
        
        Extrais uniquement la liste des postes sous format JSON.
        Les postes se trouvent dans le bordereau.
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
    cpsService.savePostes(postes);
    return jsonResponse;
}



    /**
     * Charger et indexer plusieurs fichiers PDF
     */
    public void loadFilesFournisseur(MultipartFile[] pdfFiles, String type, String fournisseur) {

        Path path = Path.of("src", "main", "resources", "storeFournisseur");
        File file = new File(path.toFile(), fileStoreFournisseur);

        for (MultipartFile pdfFile : pdfFiles) {

            // Lire le PDF et transformer en documents
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
            List<Document> documents = pdfDocumentReader.get();

            // Découper les documents en chunks
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.apply(documents);

            // Ajouter les métadonnées
            for (Document chunk : chunks) {
                chunk.getMetadata().put("type", type);
                chunk.getMetadata().put("fournisseur", fournisseur);
                chunk.getMetadata().put("fileName", pdfFile.getOriginalFilename());
            }

            // Ajouter au vector store
            vectorStore.add(chunks);
        }

        // Sauvegarder le vector store sur fichier
        vectorStore.save(file);
    }


}

