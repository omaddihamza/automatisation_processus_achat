package ma.mm.automatisation_processus_achat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.mm.automatisation_processus_achat.agents.AiAgent;
import ma.mm.automatisation_processus_achat.ocr.OcrPdfService;
import ma.mm.automatisation_processus_achat.rag.CpsService;
import ma.mm.automatisation_processus_achat.rag.DocumentIndexor;
import ma.mm.automatisation_processus_achat.rag.FournisseurService;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * @author Siham
 **/
@CrossOrigin("*")
@RestController
public class AgentController {
    private final VectorStore vectorStore;
    private final CpsService cpsService;
    private AiAgent aiAgent;
    private DocumentIndexor indexor;
    private FournisseurService fournisseurService;
    private final OcrPdfService ocrPdfService;

    public AgentController(AiAgent agent, DocumentIndexor indexor,
                           @Qualifier("aiStore")
                           VectorStore vectorStore, FournisseurService fournisseurService, CpsService cpsService
                            , OcrPdfService ocrPdfService) {
        this.aiAgent = agent;
        this.indexor = indexor;
        this.vectorStore = vectorStore;
        this.fournisseurService = fournisseurService;
        this.cpsService = cpsService;
        this.ocrPdfService = ocrPdfService;
    }

    @GetMapping(value = "/askAgent", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> askAgent(@RequestParam(defaultValue = "Hello") String query){
        return aiAgent.onQuery(query);
    }


    @PostMapping(value = "/loadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadFile(@RequestPart("file") MultipartFile file)  {
        indexor.loadFile(file);
    }

    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String askImage(@RequestParam(name="file") MultipartFile file, String question) throws IOException {
        return indexor.loadOCR(file, question);
    }

    @PostMapping(value = "/uploadCPS", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadCPS(@RequestPart("file") MultipartFile file) throws JsonProcessingException {
        return cpsService.loadFileCPS(file);
    }

    @PostMapping(value = "/uploadFournisseur",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadFournisseur(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam String fournisseur)  {
        return fournisseurService.processFournisseur(files, fournisseur);
    }



  /*  @PostMapping(value = "/ocrPDF", consumes = "multipart/form-data")
    public String extractPdf(@RequestParam("file") MultipartFile file) throws IOException {

        File tempFile = File.createTempFile("upload", ".pdf");
        file.transferTo(tempFile);

        return ocrPdfService.extractTextFromPdf(tempFile);
    }*/

    @PostMapping(value = "/pdf", consumes = "multipart/form-data", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> extractPdf(@RequestParam("file") MultipartFile file) throws IOException {

        File tempFile = File.createTempFile("upload", ".pdf");
        file.transferTo(tempFile);

        String text = ocrPdfService.extractTextFromPdf(tempFile);

        return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body(text);
    }











 /*   @PostMapping(value = "/uploadFournisseur", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFournisseur(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("fournisseur") String fournisseur) throws IOException {

        String type = "OFFER"; // type fixe pour offres

        indexor.loadFiles(files, type, fournisseur);

        return "Documents fournisseur indexés : " + files.length + " pour " + fournisseur;
    }*/


     /*   @GetMapping("/filter")
            public List<String> filterFournisseur(String query){
                List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                                .query(query)
                                .topK(4)
                                .filterExpression(Map.of("type", "CPS"))
                        .build());
                return documents.stream()
                        .map(Document::getText).collect(Collectors.toList());
            }
*/









}
