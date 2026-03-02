package ma.mm.automatisation_processus_achat.controller;

import ma.mm.automatisation_processus_achat.ocr.OcrServiceTest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//    @RequestMapping("/api/ocr")
    public class OcrController {

    private OcrServiceTest ocrServiceTest;
    private ChatClient chatClient;

    public OcrController (OcrServiceTest ocrService, ChatClient.Builder chatClient){
        this.ocrServiceTest = ocrService;
        this.chatClient = chatClient.build();

    }

    @PostMapping("/analyser-document")
    public ResponseEntity<String> analyser(@RequestParam String cheminFichier) {
        String resultatOcr = ocrServiceTest.appelerPythonPourOCR(cheminFichier);
        return ResponseEntity.ok("Document analysé avec succès ! Texte prêt pour le chat.");
    }





        @PostMapping("/chat")
        public ResponseEntity<String> poserQuestion(@RequestBody String question) {
            // 1. On récupère le texte OCR + la question
            String promptComplet = ocrServiceTest.construirePrompt(question);

            // 2. ON ENVOIE ENFIN À L'IA
            // .call() envoie la requête et récupère la réponse texte
            String reponseIA = chatClient.prompt(promptComplet).call().content();

            // 3. On renvoie la VRAIE réponse à Swagger
            return ResponseEntity.ok(reponseIA);
        }



       /* @PostMapping("/chat")
        public ResponseEntity<String> poserQuestion(@RequestBody String question) {
            // 1. On récupère le prompt complet (Données OCR + Question)
            String promptComplet = ocrServiceTest. construirePrompt(question);

            // 2. On l'envoie à l'IA (GPT-4 ou autre)
            // String reponseIA = monAiClient.call(promptComplet);

            return ResponseEntity.ok("Le prompt est prêt pour l'IA !");
        }

    @PostMapping("/analyser-document")
    public ResponseEntity<String> analyser(@RequestParam String cheminFichier) {
        // Appeler la méthode du service
        String resultatOcr = ocrServiceTest.appelerPythonPourOCR(cheminFichier);

        // Retourner le texte extrait à Swagger ou au Front
        return ResponseEntity.ok(resultatOcr);
    }*/
}
