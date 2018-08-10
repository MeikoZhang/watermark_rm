import com.alibaba.fastjson.JSON;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.sun.deploy.util.ArrayUtil;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 测试read
 */
public class TestRead {

    private static Map<String,byte[]> imageMap = new HashMap<String,byte[]>();

    private static Map<String,Integer> imageCount = new HashMap<String,Integer>();

    public static void main(String[] args) throws Exception{
        readImage();

        for(Map.Entry<String,Integer> entry : imageCount.entrySet()){
            System.out.println("key = "+entry.getKey()+"  count = "+entry.getValue());
        }
//        BufferedImage read1 = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\pdf\\4.png"));
//        ByteArrayOutputStream os1 = new ByteArrayOutputStream();
//        ImageIO.write(read1, "png", os1);
//        byte[] bytes1 = os1.toByteArray();
//
//        BufferedImage read2 = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\pdf\\8.png"));
//        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
//        ImageIO.write(read2, "png", os2);
//        byte[] bytes2 = os2.toByteArray();
//
//        BufferedImage read3 = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\pdf\\5.png"));
//        ByteArrayOutputStream os3 = new ByteArrayOutputStream();
//        ImageIO.write(read3, "png", os3);
//        byte[] bytes3 = os3.toByteArray();
//
//        System.out.println("png1 == png2 :"+Arrays.equals(bytes1,bytes2));
//        System.out.println("png1 == png3 :"+Arrays.equals(bytes1,bytes3));
    }

    public static void imgFind(String name,BufferedImage bimage) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bimage, "png", bos);
        byte[] newbytes = bos.toByteArray();

        boolean hasImg = false;
        String hasImagid = null;
        for(Map.Entry<String, byte[]> entry : imageMap.entrySet()){
            if(Arrays.equals(newbytes,entry.getValue())){
                hasImg = true;
                hasImagid = entry.getKey();
                break;
            }
        }

        if(hasImg){
            imageCount.put(hasImagid,imageCount.get(hasImagid)+1);
        }else{
            imageCount.put(name,1);
            imageMap.put(name,newbytes);
        }
    }

    public static void readImage(){

        File pdfFile = new File("C:\\Users\\Administrator\\Desktop\\pdf\\test.pdf");
        File pdfFile_out = new File("C:\\Users\\Administrator\\Desktop\\pdf\\test_out.pdf");

        PDDocument document = null;
//        PDDocument document_out = null;
        try {
            document = PDDocument.load(pdfFile);
//            document_out = PDDocument.load(pdfFile_out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages_size = document.getNumberOfPages();

        System.out.println("getAllPages==============="+pages_size);
        int j=0;

        for(int i=0;i<pages_size;i++) {
            PDPage page = document.getPage(i);
//            PDPage page1 = document_out.getPage(0);
            PDResources resources = page.getResources();
            Iterable xobjects = resources.getXObjectNames();

            if (xobjects != null) {
                Iterator imageIter = xobjects.iterator();
                while (imageIter.hasNext()) {
                    COSName key = (COSName) imageIter.next();
                    if(resources.isImageXObject(key)){
                        try {
                            PDImageXObject image = (PDImageXObject) resources.getXObject(key);

                            // 方式一：将PDF文档中的图片 分别存到一个空白PDF中。
//                            PDPageContentStream contentStream =
//                                    new PDPageContentStream(document_out,page1, PDPageContentStream.AppendMode.APPEND,
//                                            true);
//                            float scale = 1f;
//                            contentStream.drawImage(image, 20,20,image.getWidth()*scale,image.getHeight()*scale);
//                            contentStream.close();
//                            document_out.save("/Users/xiaolong/Downloads/123"+j+".pdf");
//                            System.out.println("j=" + j + "   " +image.getSuffix() + ","+
//                                    image.getHeight() +"," + image.getWidth());
                            if(j < 2){
                                System.out.println(JSON.toJSONString(image.getInterpolate()));
                            }
                            // 方式二：将PDF文档中的图片 分别另存为图片。
                            BufferedImage bimage = image.getImage();
                            FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\pdf\\"+j+".png");

                            try {
                                ImageIO.write(bimage, "png", out);
                                imgFind(j+"",bimage);
                            } catch (IOException e) {
                            } finally {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                }
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //image count
                        j++;
                    }
                }
            }
        }

        System.out.println(j);
    }


    public static void itextEdit() {
        try {
            PdfReader reader = new PdfReader("D:\\Github\\watermark_rm\\src\\a.pdf");
            OutputStream result = new FileOutputStream(new File("D:\\Github\\watermark_rm\\src\\out.pdf"));
            PdfStamper pdfStamper = new PdfStamper(reader, result);
            PdfContentStreamEditor identityEditor = new PdfContentStreamEditor();
            for(int i = 1;i <= reader.getNumberOfPages();i++){
                identityEditor.editPage(pdfStamper, i);
            }
            pdfStamper.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
