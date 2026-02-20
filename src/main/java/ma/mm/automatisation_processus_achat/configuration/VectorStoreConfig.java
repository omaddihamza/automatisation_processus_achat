package ma.mm.automatisation_processus_achat.configuration;

import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;

@Configuration
// creer un vectorStore
public class VectorStoreConfig {

    @Bean("aiStore")
    public SimpleVectorStore aiVectorStore(EmbeddingModel embeddingModel){
        return SimpleVectorStore.builder(embeddingModel).build();

    }

    @Bean("cpsStore")
    public SimpleVectorStore cpsVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean("fournisseurStore")
    public SimpleVectorStore fournisseurVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
