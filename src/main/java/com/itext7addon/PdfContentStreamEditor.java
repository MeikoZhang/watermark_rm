package com.itext7addon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfDocumentContentParser;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.pdfcleanup.PdfCleanUpEventListener;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.itextpdf.kernel.pdf.canvas.parser.EventType.RENDER_IMAGE;

public class PdfContentStreamEditor {

    final static File RESULT_FOLDER = new File("C:\\Users\\Administrator\\Desktop\\pdf", "extract");
    static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_out.pdf";
    static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    @Test
    public void testExtractCmykImage() throws Exception {
        PdfDocument document = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(document);
        for (int page = 1; page <= document.getNumberOfPages(); page++) {
            contentParser.processContent(page, new PdfCleanUpEventListener() {

            });
        }
        document.close();
    }
}