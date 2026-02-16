package ma.mm.automatisation_processus_achat;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AutomatisationProcessusAchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatisationProcessusAchatApplication.class, args);
    }
    @Bean //1. creer un vectorStore
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore vectorStore
                = SimpleVectorStore.builder(embeddingModel).build();
        return vectorStore;
    }

   /* @Bean
    public SimpleVectorStore cpsVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public SimpleVectorStore fournisseurVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }*/

}

