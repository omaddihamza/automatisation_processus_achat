package ma.mm.automatisation_processus_achat.controller;

/**
 * @author Siham
 **/

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.mm.automatisation_processus_achat.service.CpsExtractionService;
import ma.mm.automatisation_processus_achat.model.CpsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

    @RestController
    @RequestMapping("/api/extract")
    public class ExtractionController {

        private final CpsExtractionService extractionService;

        public ExtractionController(CpsExtractionService extractionService) {
            this.extractionService = extractionService;
        }

        @PostMapping(value = "/cps", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Extraire les données d'un fichier CPS PDF",
                description = "Uploadez un fichier CPS pour obtenir les informations structurées (lieu livraison, bordereau, etc.)")
        public CpsData extractCps(
                @Parameter(description = "Le fichier PDF du CPS à analyser", content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE))
                @RequestParam("file") MultipartFile file) throws JsonProcessingException {

            return extractionService.extractCpsInfo(file.getResource());
        }
    }

