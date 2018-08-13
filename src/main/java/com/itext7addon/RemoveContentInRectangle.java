package com.itext7addon;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveContentInRectangle {
    public static final String DEST = "./target/test/resources/sandbox/parse/remove_content_in_rectangle.pdf";
    public static final String SRC = "./src/test/resources/pdfs/page229.pdf";

    public static void main(String[] args) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RemoveContentInRectangle().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        //Load the license file to use cleanup features
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        List<PdfCleanUpLocationcationcation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
        cleanUpLocations.add(new PdfCleanUpLocation(1,
                new Rectangle(97, 405, 383, 40), Color.gray));

        PdfCleanUpTool cleaner = new PdfCleanUpToolpTool(pdfDoc, cleanUpLocations);
        cleaner.cleanUp();

        pdfDoc.close();
    }
}