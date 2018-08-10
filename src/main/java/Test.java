import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;


public class Test {

    public static void main(String[] args){
//        Test test = new Test();
        String srcPath="D:\\Github\\watermark_rm\\src\\a.pdf";
//        String buildPath="D:\\Github\\watermark_rm\\src\\a_out.pdf";
//        test.removeWatermark(srcPath,buildPath);
        App.extractImage(srcPath);
    }

    /**
     * 移除水印
     *
     * @param srcPath   带水印pdf
     * @param buildPath 去除水印pdf
     * @return
     */
    public String removeWatermark(String srcPath, String buildPath) {
        //注意，这将破坏所有层的文档中，只有当你没有额外的层使用
        try {
            PdfReader reader = new PdfReader(srcPath);
            //从文档中彻底删除的OCG组。
            //占位符变量
            reader.removeUnusedObjects();
            int pageCount = reader.getNumberOfPages();
            System.out.println("pdf 页数："+pageCount);
            PRStream prStream = null;
            PdfDictionary curPage;
            PdfArray contentarray;
            //循环遍历每个页面
            for (int i = 1; i <= pageCount; i++) {
                //获取页面
                curPage = reader.getPageN(i);
                //获取原始内容
                contentarray = curPage.getAsArray(PdfName.CONTENTS);
                if (contentarray != null) {
                    //循环遍历内容
                    for (int j = 0; j < contentarray.size(); j++) {
                        //获取原始字节流
                        prStream = (PRStream) contentarray.getAsStream(j);
                        // 0代表水印层
                        if (j == 0) {
                            //给它零长度和零数据删除它
                            prStream.put(PdfName.LENGTH, new PdfNumber(0));
                            prStream.setData(new byte[0]);
                        }
                    }
                }
            }
            //写出来的内容
            FileOutputStream fos = new FileOutputStream(buildPath);
            Document doc = new Document(prStream.getReader().getPageSize(2));
            PdfCopy copy = new PdfCopy(doc, fos);
            doc.open();
            for (int j = 1; j <= pageCount; j++) {
                doc.newPage();
                PdfImportedPage page = copy.getImportedPage(prStream.getReader(), j);
                copy.addPage(page);
            }
            doc.close();
            return "success";
        } catch (BadPdfFormatException e) {
            e.printStackTrace();
            return "0";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "1";
        } catch (IOException e) {
            e.printStackTrace();
            return "2";
        } catch (DocumentException e) {
            e.printStackTrace();
            return "3";
        }
    }

    public static String getFiles(String outPath, String fileName, String filePath, int num) {
        try {
            File file = new File(filePath);
            InputStream in = new FileInputStream(file);
            // 获取文件后缀名，将其作为文件类型
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,
                    fileName.length()).toLowerCase();
            if (fileType != null && !fileType.equals("")) {
                if (fileType.equals("pdf")) {
                    // 获取pdf文档
                    PDFParser parser = new PDFParser(new RandomAccessBuffer(in));
                    parser.parse();
                    PDDocument pdDocument = parser.getPDDocument();
                    PDFTextStripper stripper = new PDFTextStripper();
                    //设置是否排序
                    stripper.setSortByPosition(true);
                    //设置起始页
                    stripper.setStartPage(num);
                    //设置结束页
                    stripper.setEndPage(num);
                    String content = stripper.getText(pdDocument);
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(outPath));
                    document.open();
                    BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
                    Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, BaseColor.BLACK);
                    //建段落
                    Paragraph paragraph = new Paragraph();
                    paragraph.setFont(fontChinese);
//                    paragraph.add(paragraph); //加这一句 会报错  递归太深 报栈错误
                    paragraph.add(content);
                    //设置段落对齐方式
                    paragraph.setAlignment(Element.ALIGN_LEFT);
                    //设置缩进
                    paragraph.setIndentationLeft(100f);
                    //注意增加段落时会自动换行
                    document.add(paragraph);
                    document.close();
                    // 关闭文档
                    pdDocument.close();
                    return outPath;
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
