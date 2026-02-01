
package ma.mm.automatisation_processus_achat.config;


/**
 * @author Siham
 **/


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Stockage en mémoire, compatible H2 ou sans DB
        return new SimpleVectorStore(embeddingModel);
    }
}

