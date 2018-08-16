package com.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;


public class PDF2Image {
    public static boolean extractImages(File file, String targetFolder) {
        boolean result = true;

        try{
            PDDocument document = PDDocument.load(file);

            List<PDPage> pages = document.getDocumentCatalog().getAllPages();
            Iterator<PDPage> iter = pages.iterator();
            int count = 0;
            while( iter.hasNext()){
                PDPage page = (PDPage)iter.next();
                PDResources resources = page.getResources();
                Map<String, PDXObjectImage> images = resources.getImages();
                if(images != null)
                {
                    Iterator<String> imageIter = images.keySet().iterator();
                    while(imageIter.hasNext())
                    {
                        count++;
                        String key = (String)imageIter.next();
                        PDXObjectImage image = (PDXObjectImage)images.get( key );
                        String name = file.getName() + "_" + count;	// 图片文件名
                        image.write2file(targetFolder + name);		// 保存图片
                    }
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
            return false;
        }

        return result;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf");
        String targerFolder = "C:\\Users\\Administrator\\Desktop\\pdf\\pdfbox\\";

        extractImages(file, targerFolder);
    }
}
