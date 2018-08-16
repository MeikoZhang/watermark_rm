package com.itext7addon;

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.tool.xml.xtra.xfa.MetaData;
import com.itextpdf.tool.xml.xtra.xfa.XFAFlattener;
import com.itextpdf.tool.xml.xtra.xfa.XFAFlattenerProperties;
import com.itextpdf.tool.xml.xtra.xfa.font.XFAFontSettings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.categories.Category;

/**
 * @author Michael Demey
 */
public class FlattenXfaDocument {
    public static final String XFA = "C:\\Users\\Administrator\\Desktop\\pdf\\sample.pdf";
    public static final String DEST = "C:\\Users\\Administrator\\Desktop\\pdf\\sample_flat.pdf";

    private List<String> javascriptEvents;

    public static void main(String[] args) throws Exception {
        LicenseKey.loadLicenseFile("D:\\Github\\watermark_rm\\src\\main\\resources\\itextkey.xml");
        FlattenXfaDocument flattenXfaDocument = new FlattenXfaDocument();
        flattenXfaDocument.javascriptEvents = new ArrayList<>();
        flattenXfaDocument.javascriptEvents.add("click");
        flattenXfaDocument.manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
        XFAFlattenerProperties flattenerProperties = new XFAFlattenerProperties()
                .setPdfVersion(XFAFlattenerProperties.PDF_1_7)
                .createXmpMetaData()
                .setTagged()
//                .setExtractXdpConcurrently(false)
                .setMetaData(
                        new MetaData()
                                .setAuthor("iText Samples")
                                .setLanguage("EN")
                                .setSubject("Showing off our flattening skills")
                                .setTitle("Flattened XFA"));

        XFAFlattener xfaf = new XFAFlattener()
                .setFontSettings(new XFAFontSettings().setEmbedExternalFonts(true))
                .setExtraEventList(this.javascriptEvents)
                .setFlattenerProperties(flattenerProperties)
                .setViewMode(XFAFlattener.ViewMode.SCREEN);


        xfaf.flatten(new FileInputStream(XFA), new FileOutputStream(dest));
    }
}