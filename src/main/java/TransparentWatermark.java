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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
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
import com.itextpdf.text.pdf.PdfNumber;

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
        System.out.println("over 。。。");
    }
 
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        
        PdfPage page = pdfDoc.getPage(1);
        PdfDictionary dict = page.getPdfObject();
        
//        System.out.println(dict.size());
//        Set<Entry<PdfName, PdfObject>> entrySet = dict.entrySet();
//        for(Entry<PdfName, PdfObject> entry : entrySet){
//        	System.out.println(entry.getKey() + " :" + String.valueOf(entry.getValue().getType()));
//        	if(entry.getValue().isArray()){
//        		System.out.println(JSON.toJSONString(entry.getValue()));
//        	}
//        }
        
        PdfObject object = dict.get(PdfName.Contents);
        
//        PdfArray asArray = dict.getAsArray(PdfName.Contents);
//        if(asArray != null){
//        	//循环遍历内容
//            for(int j=0; j<asArray.size(); j++){
//                //获取原始字节流
//            	 PdfStream asStream = asArray.getAsStream(j);
//            	 isWaterMark(asStream.getBytes());
//            }
//        }
        
        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            System.out.println(stream.getAsArray(PdfName.Contents));
            byte[] data = stream.getBytes();
            byte[] waterMark = isWaterMark(data);
            if(waterMark == null){
            	stream.setData(new byte[0]);
            }else{
            	stream.setData(waterMark);
            }
        }

        pdfDoc.close();
    }
    
    private byte[] isWaterMark(byte[] data){
    	try{
        	String markerStr = new String(data,"UTF-8");
//        	System.out.println(markerStr);
        	if(markerStr.contains("WatermarkSettings")){
        		System.out.println("type : "+markerStr );
        		markerStr.replace("/Artifact <</Subtype /Watermark /Type /Pagination >>BDC ", "");
        	}
    		return markerStr.getBytes("UTF-8");
        }catch(Exception e){
        	return null;
        }
    }
}