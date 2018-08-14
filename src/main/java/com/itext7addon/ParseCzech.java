package com.itext7addon;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ParseCzech {
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_rect.txt";
    public static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";

    public static void main(String[] args) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ParseCzech().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));
        FileOutputStream fos = new FileOutputStream(DEST);

        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();

        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(pdfDoc.getFirstPage());
        byte[] array = strategy.getResultantText().getBytes("UTF-8");
        fos.write(array);

        fos.flush();
        fos.close();

        pdfDoc.close();

    }
}