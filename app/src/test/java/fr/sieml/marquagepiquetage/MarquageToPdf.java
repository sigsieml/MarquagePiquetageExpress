package fr.sieml.marquagepiquetage;

import fr.sieml.marquagepiquetage.Marquage.Marquage;
import fr.sieml.marquagepiquetage.Marquage.Techniques;
import fr.sieml.marquagepiquetage.pdf.PdfMarquageCreator;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;

public class MarquageToPdf  {

    public static final String PATH_MODELE = "modele.docx";

    @Test
    public void testMarquageToPdf() {
        Marquage marquage = new Marquage("12345",
                "Chantier 1",
                "Titulaire A",
                "dict",
                "Signataire A",
                12,
                "Rue de la Paix",
                "Paris",
                Calendar.getInstance(),
                Arrays.asList(
                        "C:\\Users\\theol\\Pictures\\channels4_profile.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\polishcow.jpg",
                        "C:\\Users\\theol\\Pictures\\gigachad.jpg"),
                true,
                true,
                true,
                true,
                false,
                null,
                23,
                new Techniques(),
                "",
                "");

        PdfMarquageCreator pdfMarquageCreator = new PdfMarquageCreator();

        try {
            InputStream doc = getClass().getClassLoader().getResourceAsStream(PATH_MODELE);
            File outputFile = new File("marquage.pdf");
            OutputStream out = new FileOutputStream(outputFile);
            PdfMarquageCreator.createPdf(null, doc, out, marquage);
            System.out.println(outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
