package com.itext7addon;

import com.alibaba.fastjson.JSON;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;
import com.sun.imageio.plugins.common.ImageUtil;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveContentInRectangle {
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_rect.pdf";
    public static final String SRC = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";

    public static void main(String[] args) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RemoveContentInRectangle().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        LicenseKey.loadLicenseFile("D:\\Github\\watermark_rm\\src\\main\\resources\\itextkey.xml");
        //Load the license file to use cleanup features
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

//        System.out.println(JSON.toJSONString(pdfDoc.getFirstPage().getPageSize()));

        float height = pdfDoc.getFirstPage().getPageSize().getHeight();
        float width = pdfDoc.getFirstPage().getPageSize().getWidth();

//        int numberOfPages = pdfDoc.getNumberOfPages();
//        for(int i = 1; i <= numberOfPages; i++){
//            List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
//            //上方区域
//            cleanUpLocations.add(new PdfCleanUpLocation(i,
//                    new Rectangle(0, 1230, 892, 33), ColorConstants.WHITE));
//            //下方区域
//            cleanUpLocations.add(new PdfCleanUpLocation(i,
//                    new Rectangle(0, 0, 892, 75), ColorConstants.WHITE));
//            //左方区域
//            cleanUpLocations.add(new PdfCleanUpLocation(i,
//                    new Rectangle(0, 0, 35, 1250), ColorConstants.WHITE));
//            //右方区域
//            cleanUpLocations.add(new PdfCleanUpLocation(i,
//                    new Rectangle(870, 0, 20, 1250), ColorConstants.WHITE));
//
//            PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDoc, cleanUpLocations);
//            cleaner.cleanUp();
//        }

        PdfPage page = pdfDoc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();
        PdfObject object = dict.get(PdfName.Reference);
        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
            if(stream.getType() == 9){
                System.out.println(JSON.toJSONString(stream.getAsDictionary(PdfName.Contents)));
                stream.setData(new String(stream.toString()).getBytes("UTF-8"));
            }else{
                stream.setData(data);
            }
        }

        pdfDoc.close();
    }
}