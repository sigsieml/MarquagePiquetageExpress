package fr.sieml.marquagepiquetage.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import fr.sieml.marquagepiquetage.Marquage.Marquage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AndroidPdfMarquageCreator{

    private Context context;
    private static final String MARQUAGE_FOLDER = "marquages";
    public AndroidPdfMarquageCreator(Context context) {
        this.context = context;
    }

    public File createPdfOnInternalStorage(Marquage marquage){
        File folder = new File(context.getFilesDir(), MARQUAGE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, "marquage.pdf");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            InputStream modele = context.getAssets().open("modele.docx");
            PdfMarquageCreator.createPdf( context,modele, fos, marquage);
            return file;
        } catch (IOException e) {
            Log.e("PdfMarquageCreator", "createPdfOnInternalStorage: " + e.getMessage() );
            Toast.makeText(context, "Erreur lors de la création du pdf", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
