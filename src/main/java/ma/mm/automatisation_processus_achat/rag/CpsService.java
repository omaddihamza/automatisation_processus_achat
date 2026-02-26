package ma.mm.automatisation_processus_achat.rag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public CpsService(
            @Qualifier("cpsStore")
            SimpleVectorStore cpsVectorStore,
            ChatClient.Builder chatClient, ObjectMapper objectMapper) {
            this.cpsVectorStore = cpsVectorStore;
            this.chatClient = chatClient.build();
            this.objectMapper = objectMapper;
    }

    public String loadFileCPS(MultipartFile pdfFile) throws JsonProcessingException {
        Path path = Path.of("src", "main", "resources", "storeCPS");
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
        /*SearchRequest request = SearchRequest.builder()
                .query("bordereau des prix ou liste des postes ou designation")
                .topK(20)
                .build();*/

         SearchRequest request = SearchRequest.builder()
                .query("bordereau des prix ou liste des postes ou designation")
                .topK(25)
                .build();

        List<Document> cpsDocs = cpsVectorStore.similaritySearch(request);

        String cpsText = cpsDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));


      /*  String prompt = """
        Voici un extrait du CPS contenant un bordereau des prix.
        
        Extrais uniquement toute la liste des postes qui se trouvent dans le bordereau
        Extrais ces postes sous format JSON.
        Si le poste  qui est dans le bordereau a une designation abreviee cherche le nom complet dans le reste des articles du cps et adapte le nom complet pour le poste
        Si dans un poste il y a d'autre accessoires, fournitures, ou des equipements accompages tu les mets dans accessoires
        Chaque poste doit contenir :
        - numero
        - designation
        - quantite
        - unite 
        - accessoires
        
        Ne retourne que du JSON valide.
        
        Texte :
        """ + cpsText;
*/


        String prompt = """
                On te fournit un extrait du CPS contenant un bordereau des prix.
                
                Ta tâche est la suivante :
                
                1. Extraire exclusivement la liste complète des postes figurant dans le bordereau des prix.
                2. Retourner le résultat uniquement au format JSON valide.
                3. Si un poste comporte une désignation abrégée, rechercher son intitulé complet dans le reste des articles du CPS et utiliser cette désignation complète adaptée au poste.
                
                Chaque poste doit être structuré comme suit :
                
                * "numero" : numéro du poste
                * "designation" : désignation complète du poste
                * "quantite" : quantité indiquée
                * "unite" : unité correspondante
                
                La réponse doit contenir uniquement un JSON valide, sans texte explicatif supplémentaire.
                
                Texte :
                """ + cpsText;



            List<Message> examples = List.of(
            new UserMessage("Prix 4 : MNVR 8 ch. \n" +
                    "Ce prix rémunère la fourniture, pose et mise en service de MNVR pour gérer l'ensemble des caméras sur le \n" +
                    "poste, l’équipement doit avoir les spécifications suivantes : \n" +
                    "Page 27 sur 35 \n" +
                    "\uF0B7 Marque : Hikvision ou similaire \n" +
                    "\uF0B7 Entrées vidéo IP : 8 canaux via PoE, extensible à 16 \n" +
                    "\uF0B7 Compression vidéo : H.265, H.264 \n" +
                    "\uF0B7 Compression audio : G.711a/G.711u/G.722.1/G.726 \n" +
                    "\uF0B7 Résolution d'enregistrement : Jusqu'à 5 MP par canal \n" +
                    "\uF0B7 Fréquence d'images : PAL : 1 à 25 fps ; NTSC : 1 à 30 fps \n" +
                    "\uF0B7 Sorties vidéo : 1x VGA, 1x sortie principale (intégrée à l'interface EXT.DEV) \n" +
                    "\uF0B7 Stockage : 2x HDD/SSD 2.5\" enfichables, 1x carte SD jusqu'à 512 Go \n" +
                    "\uF0B7 Connectivité : Module 4G enfichable, Module Wi-Fi enfichable (802.11b/g/n ou 802.11ac) \n" +
                    "\uF0B7 Positionnement : GNSS intégré (GPS, GLONASS) avec antenne FAKRA \n" +
                    "\uF0B7 Capteur : G-Sensor intégré \n" +
                    "\uF0B7 Interfaces : 8x PoE (M12 ou RJ45), 2x RJ45 10/100M, 2x USB 2.0, 2x RS-232, 1x RS-485, 4 entrées de \n" +
                    "signal haut/bas niveau, 2 sorties relais \n" +
                    "\uF0B7 Alimentation : +9 à +36 VDC \n" +
                    "\uF0B7 Consommation électrique : Veille : ≤ 0.5 W, Pleine charge : ≤ 75 W, Sans périphériques : ≤ 20 W \n" +
                    "\uF0B7 Température de fonctionnement : -25 °C à +70 °C \n" +
                    "\uF0B7 Dimensions : 202 mm × 267.6 mm × 93.9 mm \n" +
                    "\uF0B7 Avec module 4G et un module Wi-Fi pour offrir des solutions flexibles de transmission de données. \n" +
                    "NB :  \n" +
                    "1- Chaque MNVR doit être équipé par un module 4G/wifi pour assurer la communication et la \n" +
                    "transmission de donné, 2 SSD de 2TB et une boîte vidéo de stockage ignifugée de 64GB (peut protéger \n" +
                    "la sécurité des données en cas de catastrophe, comme un incendie (généralement de 260 à 1100 °C, \n" +
                    "max. 1250 °C), un choc violent (pas plus rapide que 150 km/h)). \n" +
                    "2- La licence serveur affichage inclus dans ce prix. "),
            new AssistantMessage("Les accessoires : module 4G/wifi, SSD,boîte vidéo de stockage ")
            );


        String jsonResponse = chatClient.prompt()
                .user(prompt)
                .messages(examples)
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

