package ma.mm.automatisation_processus_achat.agent1;

import java.util.ArrayList;
import java.util.List;

public class Chunker {

    // private constructor to prevent instantiation object of this class
    private Chunker() {

    };
    // split text (CPS)
    public static List<String> split(String text, int chunkSize) {
        // create container for storage chunks
        List<String> chunks = new ArrayList<>();
        // if text invariable return list empty
        if (text == null || text.isBlank()) return chunks;

        // start pointer with loop to splice all the text
        int start = 0;
        while (start < text.length()) {
            // condition if the rest of chunks less than the chunksize take it all or just chunkSize
            int end = Math.min(text.length(), start + chunkSize);
            chunks.add(text.substring(start, end));
            start = end;
        }
        // return all the chunks
        return chunks;
    }
}
