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
import java.awt.color.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.util.*;
import java.util.List;


public class Mine {

    static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\pdf100_out\\3.43-2003i.pdf";
    static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\pdf100\\3.43-2003i.pdf";

    public static void main(String[] args) throws Exception {
//        process(SRC,DEST);

        String inFileDir = "E:\\work\\文件\\pdf\\哈2\\";
        String outFileDir = "E:\\work\\文件\\pdf_out\\哈2\\";
        File outFile = new File(outFileDir);
        if(!outFile.exists()){
            outFile.mkdirs();
        }
        String[] existFiles = outFile.list();

        File inFile = new File(inFileDir);
        String[] files = inFile.list();

        System.out.println("total file num :" + files.length);
        int i = 1;
        for(String file : files){
            if(file.contains(".pdf")){
                if (Arrays.binarySearch(existFiles,file) < 0){
                    String srcFileName = inFileDir + file;
                    String outFileName = outFileDir + file;
                    System.out.println(srcFileName + " ===>" + outFileName);
                    try{
                        process(srcFileName,outFileName);
                    }catch (Exception e){
                        e.printStackTrace();
                        File deleteFile = new File(outFileName);
                        if(deleteFile.exists()){
                            deleteFile.delete();
                        }
                    }
                    System.out.println(new Date().toLocaleString() + " process over file num :" + i++);
                }
            }
        }
        System.out.println("total file complete ...");
    }

    static void process(String src,String dest) throws IOException {
        //Load the license file to use cleanup features
        //"D:\\Github\\watermark_rm\\src\\main\\resources\\itextkey.xml"
        LicenseKey.loadLicenseFile(Mine.class.getResource("/itextkey.xml").getPath());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        int numberOfPages = pdfDoc.getNumberOfPages();
        for(int i = 1; i <= numberOfPages; i++){
            PdfPage page = pdfDoc.getPage(i);
//            cleanRect(page, i);
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

    static BufferedImage bi;
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
            bi = image.getBufferedImage();
            if (bi == null) {
                continue;
            }
//
//            // 转灰度图像
//            BufferedImage grayImage = new BufferedImage(bi.getWidth(), bi.getHeight(),
//                    BufferedImage.TYPE_BYTE_GRAY);
//            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(bi, grayImage);
//
//            for(int i= 0 ; i < bi.getWidth() ; i++){
//                for(int j = 0 ; j < bi.getHeight(); j++){
//                    int rgb = grayImage.getRGB(i, j);
//                    grayImage.setRGB(i, j, rgb);
//                }
//            }

//            System.out.println("This is a image,width: " + bi.getWidth() + "  height: " + bi.getHeight());
//            Rectangle pageSize = pdfPage.getPageSize();
            //去除水印的扫描图
            Image img = makeBlackAndWhitePng(image);
            //扫描图像替换
            replaceStream(stream, img.getXObject().getPdfObject());
//            if(bi.getWidth() == (int)pageSize.getWidth()
//                    || bi.getHeight() == (int)pageSize.getHeight()
////                    || bi.getWidth() >= 150
////                    || bi.getHeight() >= 150
//                    || Math.abs(bi.getWidth()-bi.getHeight()) > 15
//                    || image.getImageBytes().length < 5000){
////                System.out.println("---------clean this image ---------------");
//                Image img = makeBlackAndWhitePng(image);
//                replaceStream(stream, img.getXObject().getPdfObject());
//            }else{
//                System.out.println("This is a image,width: " + bi.getWidth() + "  height: " + bi.getHeight());
//                System.out.println("********not clean:"+ JSON.toJSONString(image.getImageBytes().length));
//            }
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
//        saveImage(bi,"/Users/krison/Desktop/pdf/test_1.jpg");

        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
//        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(bi, newBi);

        Graphics graphics = newBi.getGraphics();
        graphics.drawImage(bi, 0, 0, null);

//        //设置画笔颜色
//        graphics.setColor(new Color(255,255,255));
//        //用上面的颜色填充背景
//        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
//        //填充文字
//        graphics.drawString("",0,0);
////        drawImage(bi, 0, 0, null);

        int[] rgb = new int[3];
        for(int y=0; y < newBi.getHeight(); y++)
            for(int x=0;x < newBi.getWidth(); x++) {
                // 根据RGB像素值进行处理
                int pixel = newBi.getRGB(x, y);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                // 去除蓝色的部分
                if(rgb[2] == 255 && rgb[0] != rgb[2] && rgb[1] != rgb[2]){
                    newBi.setRGB(x, y, new Color(255,255,255).getRGB());
                    continue;
                }

//                if(y < newBi.getHeight() * 0.25 || y> newBi.getHeight() * 0.75
//                        || x < newBi.getWidth() * 0.25 || x > newBi.getWidth() * 0.75){
//                    // 判断四边边界
//                    if(x < newBi.getWidth() * 0.04 || x > newBi.getWidth() * 0.96
//                            || y < newBi.getHeight() * 0.04 || y > newBi.getHeight() * 0.96){
//                        newBi.setRGB(x, y, new Color(255,255,255).getRGB());
//                    }
//                    // 不进行其他处理
//                    continue;
//                }

                // 判断四边边界
                // 边界处理
                if(x < newBi.getWidth() * 0.05 || x > newBi.getWidth() * 0.95
                        || y < newBi.getHeight() * 0.04 || y > newBi.getHeight() * 0.96){
                    newBi.setRGB(x, y, new Color(255,255,255).getRGB());
                    continue;
                }

                // 对超过阈值的像素进行颜色替换 - 去除水印
                if(rgb[0] > 190){
                    newBi.setRGB(x, y, new Color(255,255,255).getRGB());
                }else{
                    newBi.setRGB(x, y, bi.getRGB(x,y));
                }
//                int L = getLvalue(newBi, x, y);
//                if(L > 195){
//                    newBi.setRGB(x, y, new Color(255,255,255).getRGB());
//                }else{
//                    newBi.setRGB(x, y, bi.getRGB(x,y));
//                }
            }
        graphics.drawImage(newBi, 0, 0,null);
//        saveImage(newBi,"/Users/krison/Desktop/pdf/test_1_new.jpg");

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


    /**
     * 计算像素点的灰度
     */
    public static int getGray(Color pixel) {
//        int[] rgb = new int[3];
//        rgb[0] = (pixel & 0xff0000) >> 16;
//        rgb[1] = (pixel & 0xff00) >> 8;
//        rgb[2] = (pixel & 0xff);
//        return rgb[0] * 299/1000 + rgb[1] * 587/1000 + rgb[2] * 114/1000;
        return (pixel.getRed()*30+pixel.getGreen()*60+pixel.getBlue()*10)/100;
    }

    public static void saveImage(BufferedImage bfi, String newFileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(newFileName);
        ImageIO.write(bfi,"jpg", fos);
    }

    public static int getLvalue(BufferedImage newBi,int x,int y){
        int[] rgb = new int[3];

        int pixel = newBi.getRGB(x, y);
        rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        int L = rgb[0] * 299/1000 + rgb[1] * 587/1000 + rgb[2] * 114/1000;
        return L;
    }
}
