package com.itext7addon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {

        File file = new File("/Users/krison/Desktop/pdf/test_3.jpg");
        BufferedImage bi = ImageIO.read(file);

        BufferedImage grayImage = new BufferedImage(bi.getWidth(), bi.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
//
//        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(bi, grayImage);
        Graphics graphics = grayImage.getGraphics();
        graphics.drawImage(bi, 0, 0, null);

        int[] rgb = new int[3];
        for(int i= 0 ; i < bi.getWidth() ; i++){
            for(int j = 0 ; j < bi.getHeight(); j++){
                int pixel = bi.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                int L = rgb[0] * 299/1000 + rgb[1] * 587/1000 + rgb[2] * 114/1000;
                if(L > 190){
                    System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + ","
                            + rgb[1] + "," + rgb[2] + "  L=" + L+")");
                    grayImage.setRGB(i, j, new Color(255,255,255).getRGB());
                }else{
                    grayImage.setRGB(i, j, pixel);
                }

//                System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + ","
//                        + rgb[1] + "," + rgb[2] + "  L=" + L+")");
            }
        }
//
        graphics.drawImage(grayImage, 0, 0,null);
        File newFile = new File("/Users/krison/Desktop/pdf/test_3_pic.jpg");

        ImageIO.write(grayImage, "jpg", newFile);

    }
}
