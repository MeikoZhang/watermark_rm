package com.itext7;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;

public class MyTest2{
	public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\single.pdf";
    public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\single_out.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
//        file.getParentFile().mkdirs();
        MyTest2.manipulatePdf();
    }
    
 
    public static void manipulatePdf() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));
        PdfObject obj;
        List<Integer> streamLengths = new ArrayList<>();
        System.out.println(pdfDoc.getNumberOfPdfObjects());
        FileOutputStream fos = new FileOutputStream(DEST);
        PdfWriter pdfWriter = new PdfWriter(fos);
        for (int i = 1; i <= pdfDoc.getNumberOfPdfObjects(); i++) {
        	
            obj = pdfDoc.getPdfObject(i);
            if (obj != null && obj.isStream()) {
                byte[] b;
                try {
                	System.out.println(obj.getType());
                    b = ((PdfStream) obj).getBytes();
//                    if(i == 21){
//                    	System.out.println("--------- i:"+i +"  ---:"+new String(b,"UTF-8"));
//                    }
                } catch (PdfException exc) {
                    b = ((PdfStream)obj).getBytes(false);
                }
                System.out.println("--------- i:"+i +"  ---:"+b.length);
//                fos.write(b);
                pdfWriter.write(b);
            }
        }
        pdfWriter.close();
        fos.close();
        pdfDoc.close();
    }
}
