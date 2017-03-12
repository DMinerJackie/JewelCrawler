package com.jackie.crawler.doubanmovie.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;

public class ImageUtil {
	
//	public int getPiexPostion(int x, int y){
//		
//	}
	
	public static void main(String[] args){
		try {
			boolean load = true;
			load = false;
//			BufferedImage image = ImageIO.read(new URL("http://www.miitbeian.gov.cn/captcha.jpg")) ;
//			if(load){
//				ImageIO.write(image, "jpg", new File("E:/captcha.jpg") );
//			}else{
//				image = ImageIO.read(new File("D:\\爬虫测试\\yzm\\111.png")) ;
//			}
			BufferedImage image = ImageIO.read(new File("D:\\爬虫测试\\yzm\\11.jpg")) ;
//			image = ImageUtil.grayFilter(image);
			image = ImageUtil.binaryFilter(image);
			image = ImageUtil.lineFilter(image);
//			image = ImageUtil.lineFilter(image);
//			image = ImageUtil.line2Filter(image);
//			image = ImageUtil.point2Filter(image);
//			image = ImageUtil.lineFilter(image);
			image = ImageUtil.meanFilter(image);
//			image = ImageUtil.lineFilter(image);
//			image = ImageUtil.binaryFilter(image);
			
			
			File imageFile = new File("E:/captcha5.jpg");
//			imageFile = new File("E:/test/test.jpg");
			
			ImageIO.write(image, "jpg", imageFile);
			
			Tesseract tesseract = Tesseract.getInstance();
			tesseract.setLanguage("eng");
			String code = tesseract.doOCR(imageFile);

			System.out.println(code);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	
	/**
	 * 获取RGB的灰度
	 */
	public static int getGray(int rgb){
		//or 直接new个color对象
		Color cc = new Color(rgb);
		int r = cc.getRed();
		int g = cc.getGreen();
		int b = cc.getBlue();
		int gray = (int)(0.299 * (double)r + 0.587 * (double)g + 0.114 * (double)b);
		return gray;
	}
	
	/**
	 * 处理图片二值化
	 * @param image
	 * @return
	 */
	public static BufferedImage binaryFilter(BufferedImage image){
		
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		double redSum = 0, greenSum = 0, blueSum = 0; 
		double total = height * width;
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				Color color = new Color(image.getRGB(x, y));
				
				redSum += color.getRed();
				greenSum += color.getGreen();
				blueSum += color.getBlue();
			}
		}
		
		int means = (int)(redSum / total);
		
		for (int x = minx; x < width; x++) {
			int alpha = 0, red = 0, green = 0, blue = 0;
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				alpha = color.getAlpha();
				
				if(color.getRed() >= 20){
					red = green = blue =255;
				}else{
					red = green = blue = 0;
				}
				color = new Color(red, green, blue);
				
				outImage.setRGB(x, y, color.getRGB());
			}
		}
		
		return outImage;
	}
	/**
	 * 图片灰度处理
	 * @param image
	 * @return
	 */
	public static BufferedImage grayFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				int alpha = color.getAlpha();
				
				int gray = (int)(0.299 * (double)color.getRed() + 0.587 * (double)color.getGreen() + 0.114 * (double)color.getBlue());  
				
				color = new Color(gray, gray, gray, alpha);
				
				outImage.setRGB(x, y, color.getRGB());
			}
		}
		
		return outImage;
	}
	
	public static BufferedImage medianFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			int[] order = new int[9];
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				//处理西北位置像素
				if(x-1 >= width && y-1 >= height){
					order[0] = new Color(image.getRGB(x-1, y-1)).getRed();
				}
				//处理正北位置像素
				if(x-1 >= width && y-1 >= height){
					order[1] = new Color(image.getRGB(x-1, y)).getRed();
				}
				//处理东北位置像素
				if(x-1 >= width && y+1 <= height){
					order[2] = new Color(image.getRGB(x-1, y+1)).getRed();
				}
				//处理正东位置像素
				if(x+1 <= width-1){
					order[3] = new Color(image.getRGB(x+1, y)).getRed();
				}
				//处理东南位置像素
				if(x+1 <= width-1 && y+1 <= height-1){
					order[4] = new Color(image.getRGB(x+1, y+1)).getRed();
				}
				//处理正南位置像素
				if(y+1 <= height-1){
					order[5] = new Color(image.getRGB(x, y+1)).getRed();
				}
				//处理西南位置像素
				if(x-1 >= width && y+1 <= height-1){
					order[6] = new Color(image.getRGB(x-1, y+1)).getRed();
				}
				//处理正西位置像素
				if(x-1 >= width){
					order[7] = new Color(image.getRGB(x-1, y)).getRed();
				}
				//处理自己位置像素
				order[8] = new Color(image.getRGB(x, y)).getRed();
				
				Arrays.sort(order);
				
				int rgb = order[order.length/2];
				
				color = new Color(rgb, rgb, rgb, color.getAlpha());
//				
				outImage.setRGB(x, y, color.getRGB());
			}
		}
		
		return outImage;
	}
	/**
	 * 均值滤波
	 * @param image
	 * @return
	 */
	public static BufferedImage meanFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				int meanSum = 0;
				int count = 0;
				
				//处理西北位置像素
				if(x-1 >= width && y-1 >= height){
					meanSum += new Color(image.getRGB(x-1, y-1)).getRed();
					count++;
				}
				//处理正北位置像素
				if(x-1 >= width && y-1 >= height){
					meanSum += new Color(image.getRGB(x-1, y)).getRed();
					count++;
				}
				//处理东北位置像素
				if(x-1 >= width && y+1 <= height){
					meanSum += new Color(image.getRGB(x-1, y+1)).getRed();
					count++;
				}
				//处理正东位置像素
				if(x+1 <= width-1){
					meanSum += new Color(image.getRGB(x+1, y)).getRed();
					count++;
				}
				//处理东南位置像素
				if(x+1 <= width-1 && y+1 <= height-1){
					meanSum += new Color(image.getRGB(x+1, y+1)).getRed();
					count++;
				}
				//处理正南位置像素
				if(y+1 <= height-1){
					meanSum += new Color(image.getRGB(x, y+1)).getRed();
					count++;
				}
				//处理西南位置像素
				if(x-1 >= width && y+1 <= height-1){
					meanSum += new Color(image.getRGB(x-1, y+1)).getRed();
				}
				//处理正西位置像素
				if(x-1 >= width){
					meanSum += new Color(image.getRGB(x-1, y)).getRed();
					count++;
				}
				//处理自己位置像素
//				meanSum += new Color(image.getRGB(x, y)).getRed();
				
				if(count != 0){
					int rgb = meanSum / count;
					color = new Color(rgb, rgb, rgb, color.getAlpha());
				}
//				
				outImage.setRGB(x, y, color.getRGB());
			}
		}
		
		return outImage;
	}
	
	/**
	 * 去除杂点、杂线
	 * @param image
	 * @param length
	 * @return
	 */
	public static BufferedImage point2Filter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		Color black = new Color(0, 0, 0);
		Color white = new Color(255, 255, 255);
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				if(color.getRed() == white.getRed()){
					outImage.setRGB(x, y, color.getRGB());
					continue;
				}
				
				//先判断左右
				int self = color.getRed();
				int left_1 = ImageUtil.getColor(image, x-1, y).getRed();
				int left_2 = ImageUtil.getColor(image, x-1, y+1).getRed();
				int right_1 = ImageUtil.getColor(image, x+1, y).getRed();
				int right_2 = ImageUtil.getColor(image, x+1, y+1).getRed();
				int up_1 = ImageUtil.getColor(image, x, y-1).getRed();
				int up_2 = ImageUtil.getColor(image, x+1, y-1).getRed();
				int down_1 = ImageUtil.getColor(image, x, y+1).getRed();
				int down_2 = ImageUtil.getColor(image, x+1, y+1).getRed();
				
				int left = left_1 == 0 && left_2 == 0 ? black.getRed() : white.getRed();
				int right = right_1 == 0 && right_2 == 0 ? black.getRed() : white.getRed();
				int up = up_1 == 0 && up_2 == 0 ? black.getRed() : white.getRed();
				int down = down_1 == 0 && down_2 == 0 ? black.getRed() : white.getRed();
				
//				if(left == right && left == 0 && up == down && up != 0 && self == 0){
//					//左右为黑 上下为白
//					setRGB(outImage, x, y, white.getRGB());//自己
//					setRGB(outImage, x-1, y, white.getRGB());//左
//					setRGB(outImage, x-1, y+1, white.getRGB());//左
//					setRGB(outImage, x+1, y, white.getRGB());//右
//					setRGB(outImage, x+1, y+1, white.getRGB());//右
//				}else if(left == right && left != 0 && up == down && up == 0 && self == 0){
//					//上下为黑 左右为白
//					setRGB(outImage, x, y, white.getRGB());//自己
//					setRGB(outImage, x, y-1, white.getRGB());//上
//					setRGB(outImage, x+1, y-1, white.getRGB());//上
//					setRGB(outImage, x, y+1, white.getRGB());//下
//					setRGB(outImage, x+1, y+1, white.getRGB());//下
//				}else if(left == right && up == down && left == up && left != 0 && self == 0){
//					//上下左右均为白色
//					setRGB(outImage, x, y, white.getRGB());//自己
//					setRGB(outImage, x, y-1, white.getRGB());//上
//					setRGB(outImage, x+1, y-1, white.getRGB());//上
//					setRGB(outImage, x, y+1, white.getRGB());//下
//					setRGB(outImage, x+1, y+1, white.getRGB());//下
//					setRGB(outImage, x-1, y, white.getRGB());//左
//					setRGB(outImage, x-1, y+1, white.getRGB());//左
//					setRGB(outImage, x+1, y, white.getRGB());//右
//					setRGB(outImage, x+1, y+1, white.getRGB());//右
//				}else{
					int count = 0;
					count = left != 0 ? count+1 : count;
					count = right != 0 ? count+1 : count;
					count = up != 0 ? count+1 : count;
					count = down != 0 ? count+1 : count;
					
					if(count > 2){
						setRGB(outImage, x, y, white.getRGB());//自己
					}else{
						setRGB(outImage, x, y, black.getRGB());
					}
					
//				}
				
			}
		}
		
		return outImage;
	}
	
	/**
	 * 去除杂点
	 * @param image
	 * @param sideLength
	 * @return
	 */
	public static BufferedImage pointFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		Color black = new Color(0, 0, 0);
		Color white = new Color(255, 255, 255);
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				if(color.getRed() == white.getRed()){
					outImage.setRGB(x, y, color.getRGB());
					continue;
				}
				
				//先判断左右
				int self = color.getRed();
				int left = ImageUtil.getColor(image, x-1, y).getRed();
				int right = ImageUtil.getColor(image, x+1, y).getRed();
				int up = ImageUtil.getColor(image, x, y-1).getRed();
				int down = ImageUtil.getColor(image, x, y+1).getRed();
				
				
				if(left == right && right == up && up == down && down != 0 && self == 0){
					setRGB(outImage, x, y, white.getRGB());
				}
			}
		}
		return outImage;
	}
	
	/**
	 * 去除杂点、杂线
	 * @param image
	 * @param length
	 * @return
	 */
	public static BufferedImage line2Filter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		Color black = new Color(0, 0, 0);
		Color white = new Color(255, 255, 255);
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				if(color.getRed() == white.getRed()){
					outImage.setRGB(x, y, color.getRGB());
					continue;
				}
				
				//先判断左右
				int self_1 = color.getRed();
				int self_2 = ImageUtil.getColor(image, x+1, y).getRed();
				int self_3 = ImageUtil.getColor(image, x, y+1).getRed();
				int self_4 = ImageUtil.getColor(image, x+1, y+1).getRed();
				
				if(self_1 == self_2 && self_2 == self_3 && self_3 == self_4 && self_1 != 0){
					continue;
				}
				
				int self = 0;
				
				int left_1 = ImageUtil.getColor(image, x-1, y).getRed();
				int left_2 = ImageUtil.getColor(image, x-1, y+1).getRed();
				int right_1 = ImageUtil.getColor(image, x+2, y).getRed();
				int right_2 = ImageUtil.getColor(image, x+2, y+1).getRed();
				int up_1 = ImageUtil.getColor(image, x, y-1).getRed();
				int up_2 = ImageUtil.getColor(image, x+1, y-1).getRed();
				int down_1 = ImageUtil.getColor(image, x, y+2).getRed();
				int down_2 = ImageUtil.getColor(image, x+1, y+2).getRed();
				
				int left = left_1 == 0 && left_2 == 0 ? black.getRed() : white.getRed();
				int right = right_1 == 0 && right_2 == 0 ? black.getRed() : white.getRed();
				int up = up_1 == 0 && up_2 == 0 ? black.getRed() : white.getRed();
				int down = down_1 == 0 && down_2 == 0 ? black.getRed() : white.getRed();
				
				if(left == right && left == 0 && up == down && up != 0 && self == 0){
					//左右为黑 上下为白
					setRGB(outImage, x, y, white.getRGB());//自己
				}else if(left == right && left != 0 && up == down && up == 0 && self == 0){
					//上下为黑 左右为白
					setRGB(outImage, x, y, white.getRGB());//自己
				}
				else if(left == right && up == down && left == up && left != 0 && self == 0){
//					//上下左右均为白色
					setRGB(outImage, x, y, white.getRGB());//自己
				}
				else{
					int count = 0;
					count = left != 0 ? count+1 : count;
					count = right != 0 ? count+1 : count;
					count = up != 0 ? count+1 : count;
					count = down != 0 ? count+1 : count;
//					
					if(count > 2){
						setRGB(outImage, x, y, white.getRGB());//自己
					}else{
						setRGB(outImage, x, y, black.getRGB());
					}
					
				}
				
			}
		}
		
		return outImage;
	}
	
	/**
	 * 去除杂点、杂线
	 * @param image
	 * @param length
	 * @return
	 */
	public static BufferedImage lineFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		int minx = image.getMinX();
		int miny = image.getMinY();
		
		Color black = new Color(0, 0, 0);
		Color white = new Color(255, 255, 255);
		
		BufferedImage outImage = new BufferedImage(width, height, image.getType());
		
		for (int x = minx; x < width; x++) {
			for (int y = miny; y < height; y++) {
				
				Color color = new Color(image.getRGB(x, y));
				
				if(color.getRed() == white.getRed()){
					outImage.setRGB(x, y, color.getRGB());
					continue;
				}
				
				//先判断左右
				int self = color.getRed();
				int left = ImageUtil.getColor(image, x-1, y).getRed();
				int right = ImageUtil.getColor(image, x+1, y).getRed();
				int up = ImageUtil.getColor(image, x, y-1).getRed();
				int down = ImageUtil.getColor(image, x, y+1).getRed();
				
				if(left == right && left == 0 && up == down && up != 0 && self == 0){
					//左右为黑 上下为白
					setRGB(outImage, x, y, white.getRGB());
				}else if(left == right && left != 0 && up == down && up == 0 && self == 0){
					setRGB(outImage, x, y, white.getRGB());
				}else if(left == right && up == down && left == up && left != 0 && self == 0){
					setRGB(outImage, x, y, white.getRGB());
				}else{
					int count = 0;
					count = left != 0 ? count+1 : count;
					count = right != 0 ? count+1 : count;
					count = up != 0 ? count+1 : count;
					count = down != 0 ? count+1 : count;
//					
					if(count > 2){
						setRGB(outImage, x, y, white.getRGB());
					}else{
//						setRGB(outImage, x, y, black.getRGB());
					}
					
				}
				
			}
		}
		
		return outImage;
	}
	/**
	 * 根据坐标获取像素点的Color对象，如果像素位置位于图片外，则默认为白色
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	public static Color getColor(BufferedImage image, int x, int y){
		
		if(x < image.getMinX() || x > image.getWidth() - 1){
			return new Color(255, 255, 255);
		}
		
		if(y < image.getMinY() || y > image.getHeight() - 1){
			return new Color(255, 255, 255);
		}
		
		return new Color(image.getRGB(x, y));
	}
	
	
	/**
	 * 使用近似一维Means方法寻找二值化阈值进行二值化
	 * @param image
	 * @return
	 */
	public static BufferedImage thresholdBinaryFilter(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage outImage = new BufferedImage(width, height, image.getType());

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		
		inPixels = getPixels(image, 0, 0, width, height);
		
		int index = 0;
		int means = getThreshold(inPixels, height, width);
		
		for (int x = 0; x < width; x++) {
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for (int y = 0; y < height; y++) {
				int rgb = image.getRGB(x, y);
				Color color = new Color(rgb);
				ta = color.getAlpha();
				tr = color.getRed();
				tg = color.getGreen();
				tb = color.getBlue();
				if (tr > means) {
					tr = tg = tb = 255; // white
				} else {
					tr = tg = tb = 0; // black
				}
				outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
				outImage.setRGB(x, y, outPixels[index]);
			}
		}
		return outImage;
	}
	//获取阈值
	private static int getThreshold(int[] inPixels, int height, int width) {  
        // maybe this value can reduce the calculation consume;    
        int inithreshold = 127;  
        int finalthreshold = 0;  
        int temp[] = new int[inPixels.length];  
        for(int index=0; index<inPixels.length; index++) {  
            temp[index] = (inPixels[index] >> 16) & 0xff;  
        }  
        List<Integer> sub1 = new ArrayList<Integer>();  
        List<Integer> sub2 = new ArrayList<Integer>();  
        int means1 = 0, means2 = 0;  
        while(finalthreshold != inithreshold) {  
            finalthreshold = inithreshold;  
            for(int i=0; i<temp.length; i++) {  
                if(temp[i] <= inithreshold) {  
                    sub1.add(temp[i]);  
                } else {  
                    sub2.add(temp[i]);  
                }  
            }  
            means1 = getMeans(sub1);  
            means2 = getMeans(sub2);  
            sub1.clear();  
            sub2.clear();  
            inithreshold = (means1 + means2) / 2;  
        }  
        return finalthreshold;  
    }  
  
	private static int getMeans(List<Integer> data) {
		if(data.size() == 0){
			return 0;
		}
		int result = 0;
		int size = data.size();
		for (Integer i : data) {
			result += i;
		}
		return (result / size);
	}
	
	/**
	 * 获取像素
	 * @param image
	 * @param minX
	 * @param minY
	 * @param width
	 * @param height
	 * @return
	 */
	public static int[] getPixels(BufferedImage image, int minX, int minY, int width, int height){

		int[] pixels = new int[(width-minX) * (height - minY)];
		
		int i = 0;
		
		for (int x = minX; x < width; x++) {
			for (int y = minY; y < height; y++) {
				int rgb = image.getRGB(x, y);
				pixels[i] = rgb;
				i++;
			}
		}
		
		return pixels;
	}
	
	public static void setRGB(BufferedImage image, int x, int y, int rgb){
		if(x < image.getMinX() || x > image.getWidth() - 1){
			return;
		}
		
		if(y < image.getMinY() || y > image.getHeight() - 1){
			return;
		}
		image.setRGB(x, y, rgb);
	}
	
	private static Map<String, Integer> getRoundPiexls(BufferedImage image, int x, int y, int sideLength){
		
		Map<String, Integer> piexlMap = new HashMap<String, Integer>();
		
		if(sideLength == 2){
			getRoundPiexls(image, x, y, piexlMap);
			getRoundPiexls(image, x + 1, y, piexlMap);
			getRoundPiexls(image, x + 1, y + 1, piexlMap);
			getRoundPiexls(image, x, y + 1, piexlMap);
		}else{
			getRoundPiexls(image, x, y, piexlMap);
		}
		
//		int[] piexls = new int[piexlMap.size()];
//		
//		int i = 0;
//		for (Map.Entry<String, Integer> entry: piexlMap.entrySet()) {
//			piexls[i] = entry.getValue();
//			i++;
//		}
		
		return piexlMap;
	}
	
	private static void getRoundPiexls(BufferedImage image, int x, int y, Map<String, Integer> piexlMap){
		
		int tempX = x;
		int tempY = y;
		
		piexlMap.put("xy"+x+","+y, getColor(image, x, y).getRGB());
		
		tempX = x;
		tempY = y - 1;
		piexlMap.put(tempX+","+tempY, getColor(image, tempX, tempY).getRGB());
		tempX = x + 1;
		tempY = y;
		piexlMap.put(tempX+","+tempY, getColor(image, tempX, tempY).getRGB());
		
		tempX = x;
		tempY = y + 1;
		piexlMap.put(tempX+","+tempY, getColor(image, tempX, tempY).getRGB());
		tempX = x - 1;
		tempY = y;
		piexlMap.put(tempX+","+tempY, getColor(image, tempX, tempY).getRGB());
		
	}
}





