package ma.mm.automatisation_processus_achat.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
    private SimpleVectorStore vectorStore;

    public DocumentIndexor(SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
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
    }
}

