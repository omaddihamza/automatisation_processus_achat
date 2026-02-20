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
import java.nio.file.Path;
import java.util.List;


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
           ChatClient.Builder ChatClient, ObjectMapper objectMapper,CpsService cpsService) {
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


}

