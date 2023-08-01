package com.tlbail.marquagepiquetage.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tlbail.marquagepiquetage.Marquage;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public  class PdfMarquageCreator {



    public static void createPdf(PdfMarquageCreatorImpl provider, InputStream modele, OutputStream pdf, Marquage marquage) throws IOException {
        String formatedDate = marquage.date.get(Calendar.DAY_OF_MONTH) + "/" + marquage.date.get(Calendar.MONTH) + "/" + marquage.date.get(Calendar.YEAR);
        Map<String, String> remplacements = Map.of(
                "NUMRUE", String.valueOf(marquage.numRue) +     "   ",
                "RUEOULIEUDIT", marquage.nomRue +   "   ",
                "COMMUNE", marquage.commune +   "   ",
                "DATE", formatedDate +  "   ",
                "HEURE", marquage.date.get(Calendar.HOUR_OF_DAY) + ":" + marquage.date.get(Calendar.MINUTE) +   "   ",
                "PHOTOJOINTE", marquage.photos.size() > 0 ? "Oui" : "Aucune photo jointe"
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

        XWPFRun runSignature = tableSignataire.getRow(1).getCell(3).getParagraphs().get(0).createRun();

        // add image from
        List<String> urisImages = marquage.photos;

        for (String imageUri : urisImages) {
            try (InputStream imageStream = provider.getImagesByteFromPath(imageUri,100)) {
                // Ajoute l'image au document et récupère son id
                String id = document.addPictureData(imageStream, XWPFDocument.PICTURE_TYPE_JPEG);
                // Crée un nouveau paragraphe pour insérer l'image
                XWPFPictureData image = document.getPictureDataByID(id);


                // Read the image using ImageIO
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getData()));
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                // get the paragraph containing "Photos :"
                XWPFParagraph paragraph = null;
                for(XWPFParagraph p : document.getParagraphs()){
                    if(p.getText().contains("Photos")){
                        paragraph = p;
                        break;
                    }
                }



                paragraph.setIndentationLeft(0);
                if(Units.toEMU(width) > 7563360){
                   width = (int) Units.toPoints(7563360);
                   // respect ratio width height
                    height = (int) height * width / bufferedImage.getWidth();
                }

                // Si nous sommes à la page 2 ou plus, nous ajoutons l'image
                XWPFRun run = paragraph.createRun();

                run.addPicture(new ByteArrayInputStream(image.getData()), XWPFDocument.PICTURE_TYPE_JPEG, imageUri, Units.toEMU(width), Units.toEMU(height));

// Set the indentation
                CTPPr ppr = paragraph.getCTP().getPPr();
                if (ppr == null) {
                    ppr = paragraph.getCTP().addNewPPr();
                }
                CTInd ind = ppr.getInd();
                if (ind == null) {
                    ind = ppr.addNewInd();
                }

// Set the left indentation (values are in twips or 1/20 of a point)
                ind.setLeft(BigInteger.valueOf(-700 * 2 )); // negative value to move to the left
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }

        // signature
        if(marquage.signature != null){
            try (InputStream imageStream = provider.getImagesByteFromPath(marquage.signature,100)) {
                // Ajoute l'image au document et récupère son id
                String id = document.addPictureData(imageStream, XWPFDocument.PICTURE_TYPE_JPEG);
                // Crée un nouveau paragraphe pour insérer l'image
                XWPFPictureData image = document.getPictureDataByID(id);

                runSignature.addPicture(new ByteArrayInputStream(image.getData()), XWPFDocument.PICTURE_TYPE_JPEG, marquage.signature, Units.toEMU(100), Units.toEMU(100));

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }

        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, pdf, options);
    }



    private static void removeAllRuns(XWPFParagraph paragraph) {
        int size = paragraph.getRuns().size();
        for (int i = 0; i < size; i++) {
            paragraph.removeRun(0);
        }
    }

}
