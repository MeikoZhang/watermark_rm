/*
 
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
 
*/
 
/**
 * This example was written by Bruno Lowagie in answer to a question by a customer.
 */

import com.alibaba.fastjson.JSON;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
 
 
public class TransparentWatermark  {
    public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\test_out.pdf";
    public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\test.pdf";
 
    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new TransparentWatermark().manipulatePdf(DEST);
        System.out.println("over ¡£¡£¡£");
    }
 
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        
        PdfPage page = pdfDoc.getPage(2);
        PdfDictionary dict = page.getPdfObject();
        System.out.println(dict.size());
        Set<Entry<PdfName, PdfObject>> entrySet = dict.entrySet();
        for(Entry<PdfName, PdfObject> entry : entrySet){
        	System.out.println(entry.getKey() + " :" + String.valueOf(entry.getValue().getType()));
        	if(entry.getValue().isArray()){
        		System.out.println(JSON.toJSONString(entry.getValue()));
        	}
        }
        
        
        
        PdfObject object = dict.get(PdfName.Contents);
        if (object instanceof PdfStream) {
        	System.out.println("stream");
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
            stream.setData(new String(data).replace("Hello World", "HELLO WORLD").getBytes("UTF-8"));
        }
        
//        System.out.println(JSON.toJSONString(pdfDoc.getDocumentInfo()));
//        System.out.println(pdfDoc.getFirstPage().getContentStreamCount());
              
//        PdfCanvas under = new PdfCanvas(pdfDoc.getFirstPage().newContentStreamBefore(), new PdfResources(), pdfDoc);    
//        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createFont(FontConstants.HELVETICA));
//        Paragraph p = new Paragraph("This watermark is added UNDER the existing content")
//                .setFont(font).setFontSize(15);
//        new Canvas(under, pdfDoc, pdfDoc.getDefaultPageSize())
//                .showTextAligned(p, 297, 550, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
        
//        
//        PdfCanvas over = new PdfCanvas(pdfDoc.getFirstPage());
//        p = new Paragraph("This watermark is added ON TOP OF the existing content")
//                .setFont(font).setFontSize(15);
//        new Canvas(over, pdfDoc, pdfDoc.getDefaultPageSize())
//                .showTextAligned(p, 297, 500, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);

//        p = new Paragraph("This TRANSPARENT watermark is added ON TOP OF the existing content")
//                .setFont(font).setFontSize(15);
//        over.saveState();
//        PdfExtGState gs1 = new PdfExtGState();
//        gs1.setFillOpacity(0.5f);
//        over.setExtGState(gs1);
//        new Canvas(over, pdfDoc, pdfDoc.getDefaultPageSize())
//                .showTextAligned(p, 297, 450, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
//        over.restoreState();
        
        pdfDoc.close();
    }
}