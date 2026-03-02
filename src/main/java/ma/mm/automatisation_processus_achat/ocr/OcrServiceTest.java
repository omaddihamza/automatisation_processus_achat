package ma.mm.automatisation_processus_achat.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
@Service
public class OcrServiceTest {

    private final String OUTPUT_PATH = "C:/Users/hp/Desktop/Siham/ocr/output";

    */
/**
     * Lit tous les fichiers .md du dossier output et les fusionne.
     *//*

    public String getTexteExtrait() {
        try (Stream<Path> paths = Files.walk(Paths.get(OUTPUT_PATH))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".md"))
                    .sorted(Comparator.comparing(Path::toString))
                    .map(file -> {
                        try {
                            return Files.readString(file);
                        } catch (IOException e) {
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n---\n"));
        } catch (IOException e) {
            return "Erreur lors de la lecture des fichiers OCR : " + e.getMessage();
        }
    }

    */
/**
     * Prépare la requête finale pour l'IA.
     *//*

    public String construirePrompt(String questionUtilisateur) {
        String contexte = getTexteExtrait();

        return "Tu es un assistant intelligent. Voici les données extraites du document via OCR :\n"
                + contexte
                + "\n\nQuestion : " + questionUtilisateur;
    }

    public String appelerPythonPourOCR(String cheminFichier) {
        try {
            // Configuration de l'appel vers Python (Port 5000)
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = "{\"path\":\"" + cheminFichier.replace("\\", "/") + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/analyser"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Envoi et réception de la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Ici, tu reçois le texte complet envoyé par Python !
            return response.body();

        } catch (Exception e) {
            return "Erreur de connexion avec le script Python : " + e.getMessage();
        }
    }
*/


@Service
public class OcrServiceTest {

    // Variable pour stocker le texte le temps de la discussion
    private String dernierTexteExtrait = "";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String appelerPythonPourOCR(String cheminFichier) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = "{\"path\":\"" + cheminFichier.replace("\\", "/") + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/analyser"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // --- UTILISATION DE JACKSON  LIEU DU NETTOYAGE MANUEL ---
            JsonNode root = objectMapper.readTree(response.body());

            if (root.has("texte_ocr")) {
                this.dernierTexteExtrait = root.get("texte_ocr").asText();
                System.out.println("text " + this.dernierTexteExtrait);
                System.out.println("Extraction réussie. Longueur du texte : " + this.dernierTexteExtrait.length());
            } else {
                return "Erreur : le champ 'texte_ocr' est absent du JSON";
            }

            return this.dernierTexteExtrait;

        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }
    public String construirePrompt(String questionUtilisateur) {
        if (this.dernierTexteExtrait == null || this.dernierTexteExtrait.isEmpty()) {
            return "Erreur : Aucun document n'a été analysé au préalable.";
        }
        return "Tu es un expert en analyse de documents techniques. "
                + "Voici l'intégralité d'un document extrait par OCR (environ 20 pages). "
                + "Le texte contient des marqueurs comme '--- PAGE X ---' et des tableaux en HTML. "
                + "LIS TOUT LE CONTENU attentivement avant de répondre.\n\n"
                + "### DÉBUT DU DOCUMENT ###\n"
                + this.dernierTexteExtrait
                + "\n### FIN DU DOCUMENT ###\n\n"
                + "QUESTION DE L'UTILISATEUR : " + questionUtilisateur + "\n\n"
                + "Réponse (sois précis et cite la page si possible) :";
    }

    private String extraireTexteDuJson(String json) {
        // On retire le début {"texte_ocr":"
        String prefix = "{\"texte_ocr\":\"";
        if (json.startsWith(prefix)) {
            // On prend tout sauf le début et les deux derniers caractères "}
            String contenu = json.substring(prefix.length(), json.length() - 2);
            // On répare les sauts de ligne et les guillemets
            return contenu.replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return json;
    }

}




