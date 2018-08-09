import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            PdfReader pdfReader = new PdfReader("d:/1.pdf");
            FileOutputStream os = new FileOutputStream("d:/reader.pdf");
            PdfStamper stamper = new PdfStamper(pdfReader,os);
            PdfContentStreamEditor editor = new PdfContentStreamEditor(){
                @Override
                protected void write(PdfContentStreamProcessor processor, PdfLiteral operator, List<PdfObject> operands)
                        throws IOException {
                    String operatorString = operator.toString();
                    //Tj 操作通过当前的字体和其他文字相关的图形状态参数来取走一串操作和绘制相应的字形
                    //Tr操作设置的文本渲染模式
                    //一个文本对象开始于BT，结束于ET
                    final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj","'","\\","TJ");
                    System.out.println(operatorString);
                    if(TEXT_SHOWING_OPERATORS.contains(operatorString)){
                        PdfDictionary dic = gs().getFont().getFontDictionary();
                        if(gs().getFont().getPostscriptFontName().endsWith("BoldMT")){//BoldMT字体的名称
                            return;
                        }
                    }
                    super.write(processor, operator, operands);
                }
            };
            for(int i = 1;i <= pdfReader.getNumberOfPages();i++){
                editor.editPage(stamper, i);
            }
            stamper.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 导出PDF中的子图片
     * @param filename
     */
    public static void extractImage(String filename) {

        PdfReader reader = null;
        try {
            //读取pdf文件
            reader = new PdfReader(filename);
            //获得pdf文件的页数
            int sumPage = reader.getNumberOfPages();
            //读取pdf文件中的每一页
            for (int i = 1; i <= sumPage; i++) {
                //得到pdf每一页的字典对象
                PdfDictionary dictionary = reader.getPageN(i);
                //通过RESOURCES得到对应的字典对象
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));
                //得到XOBJECT图片对象
                PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));
                if (xobj != null) {
                    for (Iterator it = xobj.getKeys().iterator(); it.hasNext(); ) {
                        PdfObject obj = xobj.get((PdfName) it.next());
                        if (obj.isIndirect()) {
                            PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);
                            PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
                            if (PdfName.IMAGE.equals(type)) {
                                PdfObject object = reader.getPdfObject(obj);
                                if (object.isStream()) {
                                    PRStream prstream = (PRStream) object;
                                    byte[] b;
                                    try {
                                        b = reader.getStreamBytes(prstream);
                                    } catch (UnsupportedPdfException e) {
                                        b = reader.getStreamBytesRaw(prstream);
                                    }
                                    FileOutputStream output = new FileOutputStream(String.format("D:\\Github\\watermark_rm\\src\\output%d.jpg", i));
                                    output.write(b);
                                    output.flush();
                                    output.close();
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
