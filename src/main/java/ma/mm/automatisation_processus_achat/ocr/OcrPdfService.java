package ma.mm.automatisation_processus_achat.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class OcrPdfService {

    private static final String TESSDATA_PATH = "C:\\tesseract-main\\tessdata";

    public String extractTextFromPdf(File pdfFile) {

        StringBuilder fullText = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(TESSDATA_PATH);
            tesseract.setLanguage("fra");
//
            tesseract.setOcrEngineMode(1); // LSTM moderne
            tesseract.setPageSegMode(6);   // bloc uniforme de texte
//
            for (int page = 0; page < document.getNumberOfPages(); page++) {

                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);
                String pageText = tesseract.doOCR(image);

                fullText.append(pageText).append("\n");
            }


           /* for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 400);

                // prétraitement gris
                BufferedImage gray = new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY
                );
                Graphics2D g = gray.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();

                String pageText = tesseract.doOCR(gray);
                fullText.append(pageText).append("\n");
            }

            */
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement du PDF", e);
        }

        return fullText.toString();
    }
}