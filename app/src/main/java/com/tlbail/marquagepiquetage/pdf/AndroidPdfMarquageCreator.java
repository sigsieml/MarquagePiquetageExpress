package com.tlbail.marquagepiquetage.pdf;

import static com.tlbail.marquagepiquetage.pdf.PdfMarquageCreator.createPdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.tlbail.marquagepiquetage.Marquage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AndroidPdfMarquageCreator implements PdfMarquageCreatorImpl {

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
            createPdf( this,modele, fos, marquage);
            return file;
        } catch (IOException e) {
            Log.e("PdfMarquageCreator", "createPdfOnInternalStorage: " + e.getMessage() );
            Toast.makeText(context, "Erreur lors de la création du pdf", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public ByteArrayInputStream getImagesByteFromPath(String path, int quality) {
        if(path == null)
            return null;
        Uri uri = Uri.parse(path);
        // use content resolver to get image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            Log.e("PdfMarquageCreator", "getImagesByteFromPath: " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                baos.close();
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
    }

}
