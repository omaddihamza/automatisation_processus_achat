package ma.mm.automatisation_processus_achat.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

/**
 * @author Siham
 **/
@Service
public class AiAgent {
    private ChatClient chatClient;
    public AiAgent (ChatClient.Builder chatClient, ChatMemory chatMemory
//                    @Qualifier("aiStore")
//                    SimpleVectorStore vectorStore
                    ){
        this.chatClient = chatClient
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
//                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                //.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }
    public Flux<String> onQuery(@RequestParam(defaultValue = "Hello") String query){
        return chatClient.prompt()
                .user(query)
                .stream().content();
    }
}
