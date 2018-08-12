package com.itext5;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.pdf.PdfLiteral;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.Vector;
import com.itext5.PdfContentStreamEditor;

import sun.reflect.FieldInfo;

public class Test1 extends com.itext5.PdfContentStreamEditor{
	
	static List<String> TEXT_SHOWING_OPERATORS = new ArrayList<String>();
	Field textMatrixField = null;
	
	protected void Write(PdfContentStreamProcessor processor, PdfLiteral operatorLit, List<PdfObject> operands)
    {
		TEXT_SHOWING_OPERATORS.add("Tj");
		TEXT_SHOWING_OPERATORS.add("'");
		TEXT_SHOWING_OPERATORS.add("\"");
		TEXT_SHOWING_OPERATORS.add("TJ");
		
		
		textMatrixField = PdfContentStreamProcessor.class.getDeclaredField("textMatrix");
		
        if (TEXT_SHOWING_OPERATORS.contains(operatorLit.toString()))
        {
            Vector fontSizeVector = new Vector(0, gs().getFontSize(), 0);
            Matrix textMatrix = (Matrix)textMatrixField.get(this);
            Matrix curentTransformationMatrix = gs().getCtm();
            Vector transformedVector = fontSizeVector.cross(textMatrix).cross(curentTransformationMatrix);
            float transformedFontSize = transformedVector.length();
            if (transformedFontSize > 40)
                return;
        }
        Base.Write(processor, operatorLit, operands);
    }
    
    

}
