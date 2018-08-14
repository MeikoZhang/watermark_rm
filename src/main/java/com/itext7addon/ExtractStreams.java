package com.itext7addon;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractStreams {
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\extract\\streams%s.png";
    public static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";

    public static void main(String[] args) throws IOException {
        new File(DEST).getParentFile().mkdirs();
        new ExtractStreams().manipulatePdf();
    }

    public void manipulatePdf() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));
        PdfObject obj;
        List<Integer> streamLengths = new ArrayList<Integer>();
        for (int i = 1; i <= pdfDoc.getNumberOfPdfObjects(); i++) {
            obj = pdfDoc.getPdfObject(i);
            if (obj != null && obj.isStream()) {
                byte[] b;
                try {
                    b = ((PdfStream) obj).getBytes();
                } catch (PdfException exc) {
                    b = ((PdfStream)obj).getBytes(false);
                }
                System.out.println(b.length);
                FileOutputStream fos = new FileOutputStream(String.format(DEST, i));
                fos.write(b);

                streamLengths.add(b.length);
                fos.close();
            }
        }

        pdfDoc.close();
    }
}