package com.itext5;

import java.io.File;
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

public class PdfContentStreamEditor extends PdfContentStreamProcessor{

	public static void main(String[] args) throws IOException, DocumentException {
//		try {
//			PdfReader reader = new PdfReader("C:\\Users\\krison\\Desktop\\pdf\\single.pdf");
//			OutputStream result = new FileOutputStream(new File("C:\\Users\\krison\\Desktop\\pdf\\single_out5.pdf"));
//			PdfStamper pdfStamper = new PdfStamper(reader, result);
//			PdfContentStreamEditor identityEditor = new PdfContentStreamEditor();
//			for(int i = 1;i <= 1;i++){
//				identityEditor.editPage(pdfStamper, i);
//			}
//			pdfStamper.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("--------------------------self test -----------------------------------------------------------");
        PdfReader reader = new PdfReader("C:\\Users\\krison\\Desktop\\pdf\\single.pdf");
        reader.setTampered(true);
	    //从文档中彻底删除的OCG组。
	    //占位符变量
	    reader.removeUnusedObjects();
	    int pageCount = reader.getNumberOfPages();
        PdfObject obj = null;
        PRStream stream = null;
        System.out.println(reader.getXrefSize());
        int markerCount = 0;
        for (int i = 1; i <= reader.getXrefSize(); i++) {
            obj = reader.getPdfObject(i);
            if (obj != null && obj.isStream()) {
                stream = (PRStream)obj;
                try{
//                	String markerStr = new String(PdfReader.getStreamBytes(stream),"UTF-8");
                	String markerStr = new String(PdfReader.getStreamBytes(stream),"UTF-8");
                	if(markerStr.contains("WatermarkSettings")){
                		markerCount++;
                		System.out.println("i = :"+ i +"  type : "+markerStr );
                		//给它零长度和零数据删除它
                		stream.put(PdfName.LENGTH, new PdfNumber(0));
                		stream.setData(new byte[0]);
                	}
                }catch(Exception e){
                	
                }
                
//                byte[] b;
//                try {
//                    b = PdfReader.getStreamBytes(stream);
//                }
//                catch(UnsupportedPdfException e) {
//                    b = PdfReader.getStreamBytesRaw(stream);
//                }
//                FileOutputStream fos = new FileOutputStream(String.format("C:\\Users\\krison\\Desktop\\pdf\\stream%s.png", i));
//                fos.write(b);
//                fos.flush();
//                fos.close();
            }
        }
        PdfDictionary curPage;
        PdfArray contentarray;
        for(int i=1; i<=pageCount; i++){
            //获取页面
            curPage = reader.getPageN(i);
            //获取原始内容
            contentarray = curPage.getAsArray(PdfName.CONTENTS);
            if(contentarray != null){
                //循环遍历内容
                for(int j=0; j<contentarray.size(); j++){
                    //获取原始字节流
                	stream =(PRStream)contentarray.getAsStream(j);
                	// 0代表水印层
                	try{
                    	String markerStr = new String(PdfReader.getStreamBytes(stream),"UTF-8");
                    	if(markerStr.contains("WatermarkSettings")){
                    		markerCount++;
                    		System.out.println("i = :"+ i +"  type : "+markerStr );
                    		//给它零长度和零数据删除它
                    		stream.put(PdfName.LENGTH, new PdfNumber(0));
                    		stream.setData(new byte[0]);
                    	}
                    }catch(Exception e){
                    	
                    }
                }
            }
        }
        
        
        
        FileOutputStream fos = new FileOutputStream("C:\\Users\\krison\\Desktop\\pdf\\single_out.pdf");
        Document doc = new Document(stream.getReader().getPageSize(1));
        PdfCopy copy = new PdfCopy(doc, fos); 
        doc.open();
        for (int j = 1; j <= pageCount; j++) {  
        	doc.newPage();  
            PdfImportedPage page = copy.getImportedPage(stream.getReader(), j);  
            copy.addPage(page);  
        }  
        doc.close();
        System.out.println("page :"+reader.getNumberOfPages()+"   marker:"+markerCount);
        System.out.println("--------------------------self test over-----------------------------------------------------------");
	}
	
	
    /**
     * This method edits the immediate contents of a page, i.e. its content stream.
     * It explicitly does not descent into form xobjects, patterns, or annotations.
     */
    public void editPage(PdfStamper pdfStamper, int pageNum) throws IOException
    {
        PdfReader pdfReader = pdfStamper.getReader();
        PdfDictionary page = pdfReader.getPageN(pageNum);
        
        byte[] pageContentInput = ContentByteUtils.getContentBytesForPage(pdfReader, pageNum);
  
        page.remove(PdfName.CONTENTS);
        editContent(pageContentInput, page.getAsDict(PdfName.RESOURCES), pdfStamper.getUnderContent(pageNum));
        
        System.out.println(" -------------------------- ");
        HashMap<String, String> info = pdfReader.getInfo();
        for(Entry<String, String> entry : info.entrySet()){
        	System.out.println(entry.getKey()+" ---- "+entry.getValue());
        }
        
        Set<PdfName> keys = pdfReader.getPageN(1).getKeys();
        System.out.println(JSON.toJSONString(keys));
        
    }
    
    /**
     * This method processes the content bytes and outputs to the given canvas.
     * It explicitly does not descent into form xobjects, patterns, or annotations.
     */
    public void editContent(byte[] contentBytes, PdfDictionary resources, PdfContentByte canvas)
    {
        this.canvas = canvas;
        processContent(contentBytes, resources);
        this.canvas = null;
    }
    
    /**
     * <p>
     * This method writes content stream operations to the target canvas. The default
     * implementation writes them as they come, so it essentially generates identical
     * copies of the original instructions the {@link ContentOperatorWrapper} instances
     * forward to it.
     * </p>
     * <p>
     * Override this method to achieve some fancy editing effect.
     * </p> 
     */
    protected void write(PdfContentStreamProcessor processor, PdfLiteral operator, List<PdfObject> operands) throws IOException
    {
        int index = 0;
 
        for (PdfObject object : operands)
        {
            object.toPdf(canvas.getPdfWriter(), canvas.getInternalBuffer());
            canvas.getInternalBuffer().append(operands.size() > ++index ? (byte) ' ' : (byte) '\n');
        }
    }
 
    //
    // constructor giving the parent a dummy listener to talk to 
    //
    public PdfContentStreamEditor()
    {
        super(new DummyRenderListener());
    }
 
    //
    // Overrides of PdfContentStreamProcessor methods
    //
    @Override
    public ContentOperator registerContentOperator(String operatorString, ContentOperator operator)
    {
        ContentOperatorWrapper wrapper = new ContentOperatorWrapper();
        wrapper.setOriginalOperator(operator);
        ContentOperator formerOperator = super.registerContentOperator(operatorString, wrapper);
        return formerOperator instanceof ContentOperatorWrapper ? ((ContentOperatorWrapper)formerOperator).getOriginalOperator() : formerOperator;
    }
 
    @Override
    public void processContent(byte[] contentBytes, PdfDictionary resources)
    {
    	System.out.println(" process content ...");
        this.resources = resources; 
        super.processContent(contentBytes, resources);
        this.resources = null;
    }
 
    //
    // members holding the output canvas and the resources
    //
    protected PdfContentByte canvas = null;
    protected PdfDictionary resources = null;
    
    //
    // A content operator class to wrap all content operators to forward the invocation to the editor
    //
    class ContentOperatorWrapper implements ContentOperator
    {
        public ContentOperator getOriginalOperator()
        {
            return originalOperator;
        }
 
        public void setOriginalOperator(ContentOperator originalOperator)
        {
            this.originalOperator = originalOperator;
        }
 
        @Override
        public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) throws Exception
        {
            if (originalOperator != null && !"Do".equals(operator.toString()))
            {
                originalOperator.invoke(processor, operator, operands);
            }
            write(processor, operator, operands);
        }
        
        private ContentOperator originalOperator = null;
    }
 
    //
    // A dummy render listener to give to the underlying content stream processor to feed events to
    //
    static class DummyRenderListener implements RenderListener
    {
        @Override
        public void beginTextBlock() { 
        	System.out.println("beginTextBlock"); 
        }
 
        @Override
        public void renderText(TextRenderInfo renderInfo) { 
        	System.out.println("renderText : ");
        	System.out.println(renderInfo.getText());
        }
 
        @Override
        public void endTextBlock() { 
        	System.out.println("endTextBlock");
        }
 
        @Override
        public void renderImage(ImageRenderInfo renderInfo) { 
        	System.out.println("renderImage");
        	try {
				System.out.println("renderImage ,imageType : " + renderInfo.getImage().getFileType());
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} 
        	
//        	PdfImageObject image;
//        	FileOutputStream out = null;
//			try {
//				image = renderInfo.getImage();
//				out = new FileOutputStream("C:\\Users\\krison\\Desktop\\pdf\\"+UUID.randomUUID().toString()+"."+image.getFileType());
//				ImageIO.write(image.getBufferedImage(), "png", out);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			} finally {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                	
//                }
//            }
        }
    }

	
}
