package com.itext5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSON;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfLiteral;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.ContentOperator;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class PdfContentStreamEditor2 extends PdfContentStreamProcessor {

	public static final String DEST = "C:\\Users\\krison\\Desktop\\pdf\\test_out.pdf";
	public static final String SRC = "C:\\Users\\krison\\Desktop\\pdf\\test.pdf";

	public static void main(String[] args) throws IOException, DocumentException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();

		System.out.println(
				"--------------------------self test -----------------------------------------------------------");
		try {
			PdfReader reader = new PdfReader("C:\\Users\\krison\\Desktop\\pdf\\single.pdf");
	        reader.setTampered(true);
		    //从文档中彻底删除的OCG组。
		    //占位符变量
		    reader.removeUnusedObjects();
		    int pageCount = reader.getNumberOfPages();
	        PdfObject obj = null;
	        PRStream prStream = null;
	        System.out.println(reader.getXrefSize());
	        int markerCount = 0;
	        for (int i = 1; i <= reader.getXrefSize(); i++) {
	            obj = reader.getPdfObject(i);
	            if (obj != null && obj.isStream()) {
	            	prStream = (PRStream)obj;
	                try{
//	                	String markerStr = new String(PdfReader.getStreamBytes(stream),"UTF-8");
	                	String markerStr = new String(PdfReader.getStreamBytes(prStream),"UTF-8");
	                	if(markerStr.contains("WatermarkSettings")){
	                		markerCount++;
	                		System.out.println("i = :"+ i +"  type : "+markerStr );
	                		//给它零长度和零数据删除它
	                		prStream.put(PdfName.LENGTH, new PdfNumber(0));
	                		prStream.setData(new byte[0]);
	                	}
	                }catch(Exception e){
	                	
	                }
	            }
	        }
	                
	                
	                
			// 写出来的内容
			FileOutputStream fos = new FileOutputStream("C:\\Users\\krison\\Desktop\\pdf\\test_out.pdf");
			Document doc = new Document(prStream.getReader().getPageSize(1));
			PdfCopy copy = new PdfCopy(doc, fos);
			doc.open();
			for (int j = 1; j <= pageCount; j++) {
				doc.newPage();
				PdfImportedPage page = copy.getImportedPage(prStream.getReader(), j);
				copy.addPage(page);
			}
			doc.close();
			System.out.println("success");
		} catch (BadPdfFormatException e) {
			e.printStackTrace();
			System.out.println("0");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("1");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("2");
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("3");
		}

		System.out.println(
				"--------------------------self test over-----------------------------------------------------------");
	}

}
