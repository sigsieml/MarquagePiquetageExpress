package com.tlbail.marquagepiquetage;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

@RunWith(AndroidJUnit4.class)
public class wordToPdf {


    @Test
    public void wordToPdf() {
        Context  appContext = InstrumentationRegistry.getInstrumentation().getContext();
        String pdfPath = "marquage.pdf";
        try {
            XWPFDocument document = new XWPFDocument();
            document.createParagraph().createRun().setText("Hello World");
            PdfOptions options = PdfOptions.create();
            File outputFile = new File(pdfPath);
            OutputStream out = new FileOutputStream(outputFile);
            PdfConverter.getInstance().convert(document, out, options);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
