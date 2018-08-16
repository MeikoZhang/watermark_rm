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

        System.out.println(JSON.toJSONString(pdfDoc.getFirstPage().getPageSize()));

        float height = pdfDoc.getFirstPage().getPageSize().getHeight();
        float width = pdfDoc.getFirstPage().getPageSize().getWidth();

        int numberOfPages = pdfDoc.getNumberOfPages();
//        Mine.cleanRect(pdfDoc, numberOfPages);

        pdfDoc.close();
    }
}