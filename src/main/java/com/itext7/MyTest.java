package com.itext7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

public class MyTest{
//	public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\job_application.pdf";
	public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\single.pdf";
//    public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\fill_form.pdf";
    public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\single_out.pdf";


    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
//        file.getParentFile().mkdirs();
//        new MyTest().manipulatePdf(SRC, DEST);
        
        RemovetWatermarkPDF(SRC, DEST);
        
    }
    
	public static void RemovetWatermarkPDF(String sourceFile, String destinationPath) throws FileNotFoundException, IOException
    {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFile), new PdfWriter(destinationPath));
        int numberOfPages = pdfDoc.getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++)
        {
            // PdfAnnotation 
            PdfDictionary pageDict = pdfDoc.getPage(i).getPdfObject();
            PdfArray annots = pageDict.getAsArray(PdfName.Annots);
            for (int j = 0; j < annots.size(); j++)
            {
                PdfDictionary annotation = annots.getAsDictionary(j);
                if (PdfName.Watermark.equals(annotation.getAsName((PdfName.Subtype))))
                {
                    annotation.clear();
                }
            }
        }
        pdfDoc.close();
    }
	

    public void manipulatePdf(String src, String dest) throws IOException {

        //Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
      
//        PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(400, 795, 0, 0))
//        	    .setTitle(new PdfString("iText"))
//        	    .setContents("Please, fill out the form.");
//        pdfDoc.getFirstPage().addAnnotation(ann);
        
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.beginText().setFontAndSize(
                PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
                .moveText(265, 597)
                .showText("I agree to the terms and conditions.")
                .endText();
//        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
//        PdfButtonFormField checkField = PdfFormField.createCheckBox(
//                pdfDoc, new Rectangle(245, 594, 15, 15),
//                "agreement", "Off", PdfFormField.TYPE_CHECK);
//        checkField.setRequired(true);
//        form.addField(checkField);
//        
//        form.getField("reset").setAction(PdfAction.createResetForm(
//        	    new String[]{"name", "language", "experience1", "experience2",
//        	    "experience3", "shift", "info", "agreement"}, 0));
        
        pdfDoc.close();
    }
}
