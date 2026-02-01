package ma.mm.automatisation_processus_achat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        excludeName = "org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration"
)
public class AutomatisationProcessusAchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatisationProcessusAchatApplication.class, args);
    }

}
