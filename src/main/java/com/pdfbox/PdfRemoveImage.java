package com.pdfbox;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import java.io.IOException;

public class PdfRemoveImage {

    public static void main(String[] argv) throws COSVisitorException, InvalidPasswordException, CryptographyException, IOException {
        PDDocument document = PDDocument.load("C:\\Users\\Administrator\\Desktop\\pdf\\sample_out.pdf");

        if (document.isEncrypted()) {
            document.decrypt("");
        }

        PDDocumentCatalog catalog = document.getDocumentCatalog();
        for (Object pageObj :  catalog.getAllPages()) {
            PDPage page = (PDPage) pageObj;
            PDResources resources = page.findResources();
            System.out.println(resources.getImages().size());
            resources.getImages().clear();
        }

        document.save("C:\\Users\\Administrator\\Desktop\\pdf\\strippedOfImages.pdf");
    }
}
