package ma.mm.automatisation_processus_achat.rag;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OcrService {

    // L'URL de ton serveur Flask (Python/VS Code)
    private final String PYTHON_API_URL = "http://localhost:5000/analyze";

    public String callPythonOcr(String filePath) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // On prépare le JSON à envoyer : {"path": "C:\\..."}
            String jsonBody = "{\"path\":\"" + filePath.replace("\\", "\\\\") + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PYTHON_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body(); // Retourne le JSON avec le texte extrait
        } catch (Exception e) {
            return "Erreur lors de l'appel OCR : " + e.getMessage();
        }
    }
}