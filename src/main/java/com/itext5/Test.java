package com.itext5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfLiteral;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;

public class Test extends PdfContentStreamEditor{

	public static void main(String[] args) {
		try {
			PdfReader pdfReader = new PdfReader("d:/1.pdf");
			FileOutputStream os = new FileOutputStream("d:/reader.pdf");
			PdfStamper stamper = new PdfStamper(pdfReader,os);
			PdfContentStreamEditor editor = new PdfContentStreamEditor(){
				
				@Override
				protected void write(PdfContentStreamProcessor processor, PdfLiteral operator, List<PdfObject> operands) throws IOException
			    {
					String operatorString = operator.toString();
					//Tj ����ͨ����ǰ�����������������ص�ͼ��״̬������ȡ��һ�������ͻ�����Ӧ������
					//Tr�������õ��ı���Ⱦģʽ
					//һ���ı�����ʼ��BT��������ET
					final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj","'","\\","TJ");
					System.out.println(operatorString);
					if(TEXT_SHOWING_OPERATORS.contains(operatorString)){
						PdfDictionary dic = gs().getFont().getFontDictionary();						
						if(gs().getFont().getPostscriptFontName().endsWith("BoldMT")){//BoldMT���������
							return;
						}
					}
					super.write(processor, operator, operands);
			    }
			};
			for(int i = 1;i <= pdfReader.getNumberOfPages();i++){
				editor.editPage(stamper, i);
			}
			stamper.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
