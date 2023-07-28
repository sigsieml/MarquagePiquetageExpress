package com.tlbail.marquagepiquetage;

import static org.junit.Assert.assertTrue;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public class wordtoPdf {

    public static final String PATH_MODELE = "modele.docx";
    @Test
    public void testOpenFile(){
        try {
            // On ouvre le fichier
            InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_MODELE);
            // On vérifie que le fichier existe
            if (is == null) throw new AssertionError("Fichier non trouvé");
            is.close();
        } catch (IOException e) {
            assertTrue(false);
        }
    }
    @Test
    public void wordToPdf() {
        String pdfPath = "marquage.pdf";
        try {
            InputStream doc = getClass().getClassLoader().getResourceAsStream(PATH_MODELE);
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions options = PdfOptions.create();
            File outputFile = new File(pdfPath);
            OutputStream out = new FileOutputStream(outputFile);
            PdfConverter.getInstance().convert(document, out, options);
            System.out.println(outputFile.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
