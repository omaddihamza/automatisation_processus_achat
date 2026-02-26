package ma.mm.automatisation_processus_achat.rag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.mm.automatisation_processus_achat.model.Poste;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private SimpleVectorStore aiVectorStore;
    private ObjectMapper objectMapper; // Jackson
    private CpsService cpsService;
    private ChatClient chatClient;

    public DocumentIndexor(
           ChatClient.Builder ChatClient,
           @Qualifier("aiStore")
           SimpleVectorStore aiVectorStore ,
           ObjectMapper objectMapper,CpsService cpsService) {
            this.chatClient = ChatClient.build();
            this.aiVectorStore = aiVectorStore;
            this.objectMapper = objectMapper;
            this.cpsService = cpsService;
    }

    public void loadFile(MultipartFile pdfFile) {
        Path path = Path.of("src", "main", "resources", "store");
        File file = new File(path.toFile(), fileStore);
        if (file.exists()) {
            aiVectorStore.load(file);
        }
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfFile.getResource());
        //pour retourner une liste de document(chunks)
        List<Document> documents = pdfDocumentReader.get();
        //decouper les pages en chunks
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks = textSplitter.apply(documents);
        //envoyer les chunks vers simple vector store
        aiVectorStore.add(chunks);
        aiVectorStore.save(file);
        //extraire les postes du fichier CPS
        SearchRequest request = SearchRequest.builder()
                .query("bordereau des prix ou liste des postes ou designation")
                .topK(20)
                .build();
    }


    public String loadOCR(MultipartFile file, String question) throws IOException {
        byte[]bytes = file.getBytes();
        return chatClient.prompt()
                .system("Repond a la question de l'utilisateur a propos le pdf manuscrit fourni")
                .user(u->
                        u.text(question)
                                .media(MediaType.APPLICATION_PDF, new ByteArrayResource(bytes))
                ).call()
                .content();
    }

    public void imgOCR() {

        Tesseract tesseract = new Tesseract();

        try {
            // 1️⃣ Chemin vers le dossier tessdata (PAS le code source)
            tesseract.setDatapath(
                    "C:\\Users\\hp\\Downloads\\5.5.2 source code\\tesseract-ocr-tesseract-9c516f4\\tessdata"
            );

            // 2️⃣ Langue (important)
//            tesseract.setLanguage("fra+eng");

            // 3️⃣ Image à lire
            File image = new File("src/main/resources/ocr/img_1.png");

            String text = tesseract.doOCR(image);

            System.out.println("===== TEXTE OCR =====");
            System.out.println(text);

        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }

}

