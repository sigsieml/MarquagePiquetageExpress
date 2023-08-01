package com.tlbail.marquagepiquetage;

import static org.junit.Assert.assertTrue;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
        // Création des instances de la classe Marquage
        Marquage marquage = new Marquage("12345", "Chantier 1", "Titulaire A", "Signataire A", 12, "Rue de la Paix", "Paris", Calendar.getInstance(), Arrays.asList("photo1.jpg", "photo2.jpg"), true, true, true, true, false, "Signature A");
        try {
            InputStream doc = getClass().getClassLoader().getResourceAsStream(PATH_MODELE);
            File outputFile = new File("marquage.pdf");
            OutputStream out = new FileOutputStream(outputFile);
            createPdf(doc, out, marquage);
            System.out.println(outputFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void createPdf(InputStream modele, OutputStream pdf, Marquage marquage) throws IOException {
            String formatedDate = marquage.date.get(Calendar.DAY_OF_MONTH) + "/" + marquage.date.get(Calendar.MONTH) + "/" + marquage.date.get(Calendar.YEAR);
            Map<String, String> remplacements = Map.of(
                    "NUMRUE", String.valueOf(marquage.numRue) +     "   ",
                    "RUEOULIEUDIT", marquage.nomRue +   "   ",
                    "COMMUNE", marquage.commune +   "   ",
                    "DATE", formatedDate +  "   ",
                    "HEURE", marquage.date.get(Calendar.HOUR_OF_DAY) + ":" + marquage.date.get(Calendar.MINUTE) +   "   "
            );


            XWPFDocument document = new XWPFDocument(modele);
            document.getTables().get(0).getRows().get(0).getCell(1).setText(marquage.numOperation);
            document.getTables().get(0).getRows().get(1).getCell(1).setText(marquage.libelleChantier);
            document.getTables().get(0).getRows().get(2).getCell(1).setText(marquage.commune);


            XWPFTable tableElement = document.getTables().get(1);
            if(marquage.dtdict){
                tableElement.getRow(1).getCell(1).setText("X");
            }else{
                tableElement.getRow(1).getCell(2).setText("X");
            }
            if(marquage.recepisseDesDict){
                tableElement.getRow(2).getCell(1).setText("X");
            }else{
                tableElement.getRow(2).getCell(2).setText("X");
            }
            if(marquage.marquageExploitant){
                tableElement.getRow(3).getCell(1).setText("X");
            }else {
                tableElement.getRow(3).getCell(2).setText("X");
            }
            if(marquage.zoneMultiReseaux){
                tableElement.getRow(4).getCell(1).setText("X");
            }else{
                tableElement.getRow(4).getCell(2).setText("X");
            }
            if(marquage.instructionSieml){
                tableElement.getRow(5).getCell(1).setText("X");
            }else{
                tableElement.getRow(5).getCell(2).setText("X");
            }

            for(XWPFParagraph paragraph : document.getParagraphs()){
                for(String replacementKey : remplacements.keySet()){
                    if(paragraph.getText().contains(replacementKey)){
                        String text = paragraph.getText();
                        removeAllRuns(paragraph);
                        paragraph.createRun().setText(text.replace(replacementKey, remplacements.get(replacementKey)));
                    }
                }
            }

            XWPFTable tableSignataire = document.getTables().get(2);
            tableSignataire.getRow(1).getCell(0).setText(marquage.titulaire);
            tableSignataire.getRow(1).getCell(1).setText(marquage.nomSignataire);
            String formattedDateOfToday = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/" + Calendar.getInstance().get(Calendar.YEAR);
            tableSignataire.getRow(1).getCell(2).setText(formattedDateOfToday);


            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, pdf, options);
    }

    private void removeAllRuns(XWPFParagraph paragraph) {
        int size = paragraph.getRuns().size();
        for (int i = 0; i < size; i++) {
            paragraph.removeRun(0);
        }
    }
}
