package com.jackie.crawler.doubanmovie.ocr;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(new File("D:\\爬虫测试\\yzm\\yzm.png"))) ;
//			BufferedImage image = ImageIO.read(new File("E:/captcha.jpg")) ;
//			ImageFilter filter = new ImageFilter(image);
//			image = filter.grayFilter();
			
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//			filter = new ImageFilter(image);
//			image = filter.median();
//
////			filter = new ImageFilter(image);
////			image = filter.scaling(4);
//
////			filter = new ImageFilter(image);
////			image = filter.sharp();
			
//			filter = new ImageFilter(image);
//			image = filter.lineGrey();
			ImageIO.write(image, "png", new File("E:/captcha1.png"));
			
			String maybe2 = new OCR().recognizeText(new File("E:/captcha1.png"), "png");
			System.out.println(maybe2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
