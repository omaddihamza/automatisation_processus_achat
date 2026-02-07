package ma.mm.automatisation_processus_achat.controller;

import ma.mm.automatisation_processus_achat.agents.AiAgent;
import ma.mm.automatisation_processus_achat.rag.DocumentIndexor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

/**
 * @author Siham
 **/
@CrossOrigin("*")
@RestController
public class AgentController {
    private AiAgent aiAgent;
    private DocumentIndexor indexor;
    public AgentController(AiAgent agent, DocumentIndexor indexor) {
        this.aiAgent = agent;
        this.indexor = indexor;
    }

    @GetMapping(value = "/askAgent", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> askAgent(@RequestParam(defaultValue = "Hello") String query){
        return aiAgent.onQuery(query);
    }


    @PostMapping(value = "/loadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void loadFile(@RequestPart("file") MultipartFile file)  {
        indexor.loadFile(file);
    }


}
