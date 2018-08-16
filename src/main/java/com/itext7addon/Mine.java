package com.itext7addon;

import com.alibaba.fastjson.JSON;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.element.Image;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.itext7addon.ReplaceImage.makeBlackAndWhitePng;
import static com.itext7addon.ReplaceImage.replaceStream;
import static org.bouncycastle.crypto.tls.CipherType.stream;

public class Mine {

    static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_out.pdf";
    static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\test_e.pdf";

    public static void main(String[] args) throws Exception {

        File dest = new File(DEST);
        dest.getParentFile().mkdirs();
        File src = new File(SRC);

        String inFileDir = "C:\\Users\\Administrator\\Desktop\\pdf\\";
        String outFileDir = "C:\\Users\\Administrator\\Desktop\\out\\";
        File inFile = new File(inFileDir);
        File[] files = inFile.listFiles();
        for(File file : files){
            if(file.getName().contains(".pdf")){
                String srcFileName = inFileDir + file.getName();
                String outFileName = outFileDir + file.getName();
                System.out.println(srcFileName + " ===>" + outFileName);
                process(SRC,DEST);
            }
        }

//
    }

    static void process(String src,String dest) throws IOException {
        //Load the license file to use cleanup features
        //"D:\\Github\\watermark_rm\\src\\main\\resources\\itextkey.xml"
        LicenseKey.loadLicenseFile(Mine.class.getResource("/itextkey.xml").getPath());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        int numberOfPages = pdfDoc.getNumberOfPages();
        for(int i = 1; i <= numberOfPages; i++){
            PdfPage page = pdfDoc.getPage(i);
            cleanRect(page, i);
            cleanImage(page);
        }

        pdfDoc.close();
    }

    static void cleanRect(PdfPage pdfPage, int numberOfPage) throws IOException {

        int top = 33;
        int bottom = 75;
        int left = 35;
        int right = 25;
        Rectangle pageSize = pdfPage.getPageSize();

        List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
        //上方区域
        cleanUpLocations.add(new PdfCleanUpLocation(numberOfPage,
                new Rectangle(0, pageSize.getHeight() - top, pageSize.getWidth(), top), ColorConstants.WHITE));
        //下方区域
        cleanUpLocations.add(new PdfCleanUpLocation(numberOfPage,
                new Rectangle(0, 0, pageSize.getWidth(), bottom), ColorConstants.WHITE));
        //左方区域
        cleanUpLocations.add(new PdfCleanUpLocation(numberOfPage,
                new Rectangle(0, 0, left, pageSize.getHeight()), ColorConstants.WHITE));
        //右方区域
        cleanUpLocations.add(new PdfCleanUpLocation(numberOfPage,
                new Rectangle(pageSize.getWidth() - right, 0, right, pageSize.getHeight()), ColorConstants.WHITE));

        PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfPage.getDocument(), cleanUpLocations);
        cleaner.cleanUp();
    }

    static void cleanImage(PdfPage pdfPage) throws IOException {
        PdfDictionary page = pdfPage.getPdfObject();
        PdfDictionary resources = page.getAsDictionary(PdfName.Resources);
        PdfDictionary xobjects = resources.getAsDictionary(PdfName.XObject);
        Iterator<PdfName> iterator = xobjects.keySet().iterator();
        while (iterator.hasNext()) {
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
//            System.out.println("This is a image,width: " + bi.getWidth() + "  height: " + bi.getHeight());
            Rectangle pageSize = pdfPage.getPageSize();

            if(bi.getWidth() == (int)pageSize.getWidth()
                    || bi.getHeight() == (int)pageSize.getHeight()
//                    || bi.getWidth() >= 150
//                    || bi.getHeight() >= 150
                    || Math.abs(bi.getWidth()-bi.getHeight()) > 15
                    || image.getImageBytes().length < 5000){
//                System.out.println("---------clean this image ---------------");
                Image img = makeBlackAndWhitePng(image);
                replaceStream(stream, img.getXObject().getPdfObject());
            }else{
                System.out.println("This is a image,width: " + bi.getWidth() + "  height: " + bi.getHeight());
                System.out.println("********not clean:"+JSON.toJSONString(image.getImageBytes().length));
            }
        }
    }


    /**
     * 画白色背景图
     * @param image
     * @return
     * @throws IOException
     */
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


    /**
     * 图片替换
     * @param orig
     * @param stream
     * @throws IOException
     */
    public static void replaceStream(PdfStream orig, PdfStream stream) throws IOException {
        orig.clear();

        for (PdfName name : stream.keySet()) {
            orig.put(name, stream.get(name));
        }
        orig.setData(stream.getBytes());
    }
}
