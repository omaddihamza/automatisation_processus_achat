package ma.mm.automatisation_processus_achat.agent1;

import org.springframework.ai.document.Document;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PgVectorRagStorage {
    // interface in spring AI
    public final VectorStore vectorStore;

    public PgVectorRagStorage(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // Storage CPS after chunks
    public void storeCps(String docId, List<String> chunks) {
        List<Document> documents = new ArrayList<>();

        // loop of all chunks
        for (int i = 0; i < chunks.size(); i++) {
            documents.add(
                    new Document(
                            chunks.get(i),
                            Map.of(
                                    "docId", docId,
                                    "chunkId", i)));
        }
        // embeddings text , storage in pgvector
        vectorStore.add(documents);
    }
     //RAG Searsh in cps
    public List<Document> search(String docId, String question) {

        // make search inside cps
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5) // return to 5 chunks that near to the question
                // search in the docID
                .filterExpression("docId == '" + docId + "'")
                .build();

        return vectorStore.similaritySearch(request);
    }

    }

