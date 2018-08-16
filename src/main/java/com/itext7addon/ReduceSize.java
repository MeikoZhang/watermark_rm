package com.itext7addon;

/**
 * This example was written by Bruno Lowagie in answer to the following question:
 * http://stackoverflow.com/questions/30483622/compressing-images-in-existing-pdfs-makes-the-resulting-pdf-file-bigger-lowagie
 */
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class ReduceSize {
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\test_e_out.pdf";
    public static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\test_e.pdf";
    public static final float FACTOR = 1.0f;

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ReduceSize().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
//        PdfWriter writer = new PdfWriter(DEST, new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        System.out.println(pdfDoc.getPage(1).getPageSize());
        PdfObject object;
        PdfStream stream;
        for (PdfIndirectReference indRef : pdfDoc.listIndirectReferences()) {
            object = indRef.getRefersTo();
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PdfStream) object;
            if (!PdfName.Image.equals(stream.getAsName(PdfName.Subtype))) {
                continue;
            }
            PdfImageXObject image = new PdfImageXObject(stream);
            BufferedImage bi = image.getBufferedImage();
            if (bi == null) {
                continue;
            }
            System.out.println("This is a image,width: "+bi.getWidth()+"  height: "+bi.getHeight());
//            int width = (int) (bi.getWidth() * FACTOR);
//            int height = (int) (bi.getHeight() * FACTOR);
            int width = (int) (bi.getWidth());
            int height = (int) (bi.getHeight());
//            if (width <= 0 || height <= 0) {
//                continue;
//            }

            if (width == 190 && height == 197) {
                continue;
            }

//            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
//            Graphics2D g = img.createGraphics();
//            g.drawRenderedImage(bi, at);
//            ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
//            ImageIO.write(img, "JPG", imgBytes);

            BufferedImage img = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
            img.getGraphics().drawImage(bi, 0, 0, null);
            ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
            ImageIO.write(img, "png", imgBytes);

            System.out.println("---------------clear this img-----------");
            stream.clear();
//            stream.put(PdfName.Length, new PdfNumber(0));
//            stream.setData(new byte[0]);
            stream.setData(imgBytes.toByteArray());
            stream.put(PdfName.Type, PdfName.XObject);
            stream.put(PdfName.Subtype, PdfName.Image);
            stream.put(PdfName.Filter, PdfName.DCTDecode);
            stream.put(PdfName.Width, new PdfNumber(1));
            stream.put(PdfName.Height, new PdfNumber(1));
            stream.put(PdfName.BitsPerComponent, new PdfNumber(8));
            stream.put(PdfName.ColorSpace, PdfName.DeviceRGB);
        }
        pdfDoc.close();
    }
}