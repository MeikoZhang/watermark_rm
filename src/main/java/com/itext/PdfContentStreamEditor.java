package com.itext;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.ocg.OCGParser;
import com.itextpdf.text.pdf.ocg.OCGRemover;
import com.itextpdf.text.pdf.parser.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * <a href="http://stackoverflow.com/questions/35526822/removing-watermark-from-pdf-itextsharp">
 * Removing Watermark from PDF iTextSharp
 * </a>
 * <p>
 * This class presents a simple content stream editing framework. As is it creates an equivalent
 * copy of the original page content stream. To actually edit, simply overwrite the method
 * {@link #write(PdfContentStreamProcessor, PdfLiteral, List)} to not (as in this class) write
 * the given operations as they are but change them in some fancy way.
 * </p>
 * 去除PDF文件的水印字体
 * @author mkl
 */
public class PdfContentStreamEditor extends PdfContentStreamProcessor
{

    public static void main(String[] args) {
        try {
            PdfReader reader = new PdfReader("C:\\Users\\krison\\Desktop\\pdf\\single.pdf");
            OutputStream result = new FileOutputStream(new File("C:\\Users\\krison\\Desktop\\pdf\\single_out.pdf"));
            PdfStamper pdfStamper = new PdfStamper(reader, result);
            Map<String, PdfLayer> pdfLayers = pdfStamper.getPdfLayers();
            ////除导出的图层以外，删除其他所有图层
            for(String key : pdfLayers.keySet()){
                System.out.println(key);
                if(!key.contains("UV"))
                {	
                	System.out.println(" find Watermark");
                	PdfLayer pdfLayer = pdfLayers.get(key);
                	System.out.println(pdfStamper.getOverContent(1));
                	
                	IOUtils.write(pdfLayer.getBytes(), new FileOutputStream(new File("C:\\Users\\krison\\Desktop\\pdf\\layers.png")));
                	
                	OCGRemover oCGRemover = new OCGRemover();
                    oCGRemover.removeLayers(pdfStamper.getReader(), key);
                }
            }

//            com.itext.PdfContentStreamEditor identityEditor = new com.itext.PdfContentStreamEditor();
//            for(int i = 1;i <= reader.getNumberOfPages();i++){
//            	System.out.println(" edit page");
//                identityEditor.editPage(pdfStamper, i);
//            }
            pdfStamper.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    static class DummyRenderListener implements RenderListener {
        @Override
        public void beginTextBlock() { }

        @Override
        public void renderText(TextRenderInfo renderInfo) { }

        @Override
        public void endTextBlock() { }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) { }
    }
}