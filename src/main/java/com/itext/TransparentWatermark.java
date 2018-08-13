package com.itext;/*
 
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
 
*/
 
/**
 * This example was written by Bruno Lowagie in answer to a question by a customer.
 */

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;


public class TransparentWatermark  {
    public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\test_out.pdf";
    public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\test.pdf";
 
    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new TransparentWatermark().manipulatePdf(DEST);
        System.out.println("over ������");
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
//        	//ѭ����������
//            for(int j=0; j<asArray.size(); j++){
//                //��ȡԭʼ�ֽ���
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