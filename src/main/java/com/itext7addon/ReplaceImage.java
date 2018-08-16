package com.itext7addon;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.element.Image;
import com.sun.imageio.plugins.common.ImageUtil;
import org.junit.experimental.categories.Category;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;


public class ReplaceImage{
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_out.pdf";
    public static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_edit.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ReplaceImage().manipulatePdf(DEST);
    }

    public static Image makeBlackAndWhitePng(PdfImageXObject image) throws IOException {
        BufferedImage bi = image.getBufferedImage();
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = newBi.getGraphics();
        //设置画笔颜色
        graphics.setColor(new Color(255,255,255));
        //用上面的颜色填充背景
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        //填充文字
        graphics.drawString("",0,0);
//        drawImage(bi, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", baos);
        return new Image(ImageDataFactory.create(baos.toByteArray()));
    }

    public static void replaceStream(PdfStream orig, PdfStream stream) throws IOException {
        orig.clear();

        for (PdfName name : stream.keySet()) {
            orig.put(name, stream.get(name));
        }
        orig.setData(stream.getBytes());
    }

    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));
        PdfDictionary page = pdfDoc.getFirstPage().getPdfObject();

        PdfDictionary resources = page.getAsDictionary(PdfName.Resources);
        PdfDictionary xobjects = resources.getAsDictionary(PdfName.XObject);
        Iterator<PdfName> iterator = xobjects.keySet().iterator();
        while(iterator.hasNext()){
            PdfName imgRef = iterator.next();
            PdfStream stream = xobjects.getAsStream(imgRef);

            if (!PdfName.Image.equals(stream.getAsName(PdfName.Subtype))) {
                continue;
            }
            PdfImageXObject image = new PdfImageXObject(stream);
            BufferedImage bi = image.getBufferedImage();
            if (bi == null) {
                continue;
            }
            System.out.println("This is a image,width: "+bi.getWidth()+"  height: "+bi.getHeight());
//
//            BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
//            newBi.getGraphics().drawImage(bi, 0, 0, null);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(newBi, "png", baos);
//
//            stream.clear();
//            stream.setData(baos.toByteArray());
//            stream.put(PdfName.Type, PdfName.XObject);
//            stream.put(PdfName.Subtype, PdfName.Image);
//            stream.put(PdfName.Filter, PdfName.DCTDecode);
//            stream.put(PdfName.Width, new PdfNumber(10));
//            stream.put(PdfName.Height, new PdfNumber(10));
//            stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
//            stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);

            Image img = makeBlackAndWhitePng(image);
            System.out.println(img.getXObject().getPdfObject());
            replaceStream(stream, img.getXObject().getPdfObject());
        }

        pdfDoc.close();
    }
}