package fr.sieml.marquagepiquetage.pdf;

import java.io.ByteArrayInputStream;

public interface PdfMarquageCreatorImpl {
    ByteArrayInputStream getImagesByteFromPath(String path, int quality);
}