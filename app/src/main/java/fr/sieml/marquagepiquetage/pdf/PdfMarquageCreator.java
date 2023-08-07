package fr.sieml.marquagepiquetage.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import fr.sieml.marquagepiquetage.Marquage.Marquage;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public  class PdfMarquageCreator {



    public static void createPdf(Context context, InputStream modele, OutputStream pdf, Marquage marquage) throws IOException {
        String formatedDate = marquage.date.get(Calendar.DAY_OF_MONTH) + "/" + marquage.date.get(Calendar.MONTH) + "/" + marquage.date.get(Calendar.YEAR);
        Map<String, String> remplacements = new HashMap<>();
        remplacements.put("NUMRUE", marquage.numRue + "   ");
        remplacements.put("FINRUE", marquage.numRueFin + "   ");
        remplacements.put("RUEOULIEUDIT", marquage.nomRue + "   ");
        remplacements.put("COMMUNE", marquage.commune + "   ");
        remplacements.put("DATE", formatedDate + "   ");
        remplacements.put("HEURE", marquage.date.get(Calendar.HOUR_OF_DAY) + ":" + marquage.date.get(Calendar.MINUTE) + "   ");
        remplacements.put("PHOTOJOINTE", marquage.photos.size() > 0 ? "Oui" : "Aucune photo jointe");
        remplacements.put("CHANTIERDURATION", marquage.chantierDuration + "   ");
        remplacements.put("OBSERVATIONSCHANTIER", marquage.observation + "   ");


        XWPFDocument document = new XWPFDocument(modele);
        document.getTables().get(0).getRows().get(0).getCell(1).setText(marquage.numOperation);
        document.getTables().get(0).getRows().get(1).getCell(1).setText(marquage.libelleChantier);
        document.getTables().get(0).getRows().get(2).getCell(1).setText(marquage.commune);
        document.getTables().get(0).getRows().get(3).getCell(1).setText(marquage.numDict);


        XWPFTable tableElement = document.getTables().get(1);
        if(marquage.dtdict){
            tableElement.getRow(0).getCell(1).setText("X");
        }else{
            tableElement.getRow(0).getCell(1).setText("");
        }
        if(marquage.recepisseDesDict){
            tableElement.getRow(1).getCell(1).setText("X");
        }else{
            tableElement.getRow(1).getCell(1).setText("");
        }
        if(marquage.marquageExploitant){
            tableElement.getRow(2).getCell(1).setText("X");
        }else {
            tableElement.getRow(2).getCell(1).setText("");
        }
        if(marquage.zoneMultiReseaux){
            tableElement.getRow(3).getCell(1).setText("X");
        }else{
            tableElement.getRow(3).getCell(1).setText("");
        }
        if(marquage.instructionSieml){
            tableElement.getRow(4).getCell(1).setText("X");
        }else{
            tableElement.getRow(4).getCell(1).setText("");
        }

        XWPFTable tableTechniques = document.getTables().get(2);
        if(marquage.techniques.forageAvecTariere){
            tableTechniques.getRow(0).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(0).getCell(1).setText("");
        }
        if(marquage.techniques.forageDirige){
            tableTechniques.getRow(1).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(1).getCell(1).setText("");
        }
        if(marquage.techniques.fuseOuOgive){
            tableTechniques.getRow(2).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(2).getCell(1).setText("");
        }
        if(marquage.techniques.briseRoche){
            tableTechniques.getRow(3).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(3).getCell(1).setText("");
        }
        if(marquage.techniques.enginElevateur){
            tableTechniques.getRow(4).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(4).getCell(1).setText("");
        }
        if(marquage.techniques.enginVibrant){
            tableTechniques.getRow(5).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(5).getCell(1).setText("");
        }
        if(marquage.techniques.grue){
            tableTechniques.getRow(6).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(6).getCell(1).setText("");
        }
        if(marquage.techniques.manuelOuManutentionDobjetOuMateriel){
            tableTechniques.getRow(7).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(7).getCell(1).setText("");
        }
        if(marquage.techniques.pelleMecanique){
            tableTechniques.getRow(8).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(8).getCell(1).setText("");
        }
        if(marquage.techniques.trancheuse){
            tableTechniques.getRow(9).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(9).getCell(1).setText("");
        }
        if(marquage.techniques.raboteuse){
            tableTechniques.getRow(10).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(10).getCell(1).setText("");
        }
        if(marquage.techniques.techniqueDouce){
            tableTechniques.getRow(11).getCell(1).setText("X");
        }else{
            tableTechniques.getRow(11).getCell(1).setText("");
        }

        tableTechniques.getRow(12).getCell(1).setText(marquage.autreEnginDeChantier);


        for(XWPFParagraph paragraph : document.getParagraphs()){
            for(String replacementKey : remplacements.keySet()){
                if(paragraph.getText().contains(replacementKey)){
                    String text = paragraph.getText();
                    removeAllRuns(paragraph);
                    paragraph.createRun().setText(text.replace(replacementKey, remplacements.get(replacementKey)));
                }
            }
        }

        XWPFTable tableSignataire = document.getTables().get(3);
        tableSignataire.getRow(1).getCell(0).setText(marquage.titulaire);
        tableSignataire.getRow(1).getCell(1).setText(marquage.nomSignataire);
        String formattedDateOfToday = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/" + Calendar.getInstance().get(Calendar.YEAR);
        tableSignataire.getRow(1).getCell(2).setText(formattedDateOfToday);

        XWPFRun runSignature = tableSignataire.getRow(1).getCell(3).getParagraphs().get(0).createRun();


        // get the paragraph containing "Photos :"
        XWPFParagraph paragraph = null;
        for(XWPFParagraph p : document.getParagraphs()){
            if(p.getText().contains("Photos")){
                paragraph = p;
                break;
            }
        }
        paragraph.setIndentationLeft(0);

        // add image from
        List<String> urisImages = marquage.photos;
        for (String imageUri : urisImages) {
            Uri uri = Uri.parse(imageUri);
            try {
                InputStream imageStream = context.getContentResolver().openInputStream(uri);
                // rotate l'image dans le bon sens
                ExifInterface exif = new ExifInterface(imageStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                imageStream.close();


                imageStream = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                bitmap = rotateBitmap(bitmap, orientation);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                // Ajoute l'image au document et récupère son id
                String id = document.addPictureData(bos.toByteArray(), XWPFDocument.PICTURE_TYPE_JPEG);
                XWPFPictureData image = document.getPictureDataByID(id);

                // Définir la largeur et la hauteur maximales de l'image en points (1 pouce = 72 points)
                int maxWidth = 500; // Ajustez cette valeur en fonction de la largeur de votre document
                int maxHeight = 600; // Ajustez cette valeur en fonction de la hauteur maximale souhaitée pour vos images

                // Calculez le rapport d'aspect de l'image
                double aspectRatio = (double) bitmap.getWidth() / (double) bitmap.getHeight();

                // Calculez la nouvelle largeur et la nouvelle hauteur de l'image
                int newWidth = Math.min(bitmap.getWidth(), maxWidth);
                int newHeight = (int) (newWidth / aspectRatio);

                // Si la nouvelle hauteur est toujours trop grande, ajustez la largeur en conséquence
                if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = (int) (newHeight * aspectRatio);
                }

                // Si nous sommes à la page 2 ou plus, nous ajoutons l'image
                XWPFRun run = paragraph.createRun();
                run.setText("     \n\n\n  ");

                run.addPicture(new ByteArrayInputStream(image.getData()), XWPFDocument.PICTURE_TYPE_JPEG, imageUri, Units.toEMU(newWidth), Units.toEMU(newHeight));

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
                ind.setLeft(BigInteger.valueOf(-1200)); // negative value to move to the left
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }

        // signature
        if(marquage.signature != null){
            try (InputStream imageStream = context.getContentResolver().openInputStream(Uri.parse(marquage.signature))) {
                // Ajoute l'image au document et récupère son id
                String id = document.addPictureData(imageStream, XWPFDocument.PICTURE_TYPE_PNG);
                // Crée un nouveau paragraphe pour insérer l'image
                XWPFPictureData image = document.getPictureDataByID(id);

                runSignature.addPicture(new ByteArrayInputStream(image.getData()), XWPFDocument.PICTURE_TYPE_PNG, marquage.signature, Units.toEMU(100), Units.toEMU(100));

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


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }



}
