package com.itext7addon;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mine {

    static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_out.pdf";
    static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";

    public static void main(String[] args) throws Exception {

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        PdfPage page = pdfDoc.getPage(1);

    }
}
