package com.tlbail.marquagepiquetage;

import static com.tlbail.marquagepiquetage.pdf.PdfMarquageCreator.createPdf;

import com.tlbail.marquagepiquetage.pdf.PdfMarquageCreator;
import com.tlbail.marquagepiquetage.pdf.PdfMarquageCreatorImpl;

import org.apache.poi.util.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
                null);

        PdfMarquageCreator pdfMarquageCreator = new PdfMarquageCreator();

        try {
            InputStream doc = getClass().getClassLoader().getResourceAsStream(PATH_MODELE);
            File outputFile = new File("marquage.pdf");
            OutputStream out = new FileOutputStream(outputFile);
            createPdf(new PdfMarquageCreatorImpl() {
                @Override
                public ByteArrayInputStream getImagesByteFromPath(String path, int quality) {
                    File imageFile = new File(path);
                    try {
                        return new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(imageFile)));
                    } catch (IOException e) {
                        System.err.println("Error while reading image file" + e.getMessage());
                        return null;
                    }
                }
            }, doc, out, marquage);
            System.out.println(outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
