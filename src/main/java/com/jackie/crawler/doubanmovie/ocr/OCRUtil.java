package com.jackie.crawler.doubanmovie.ocr;

import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.io.IOUtils;
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

public class OCRUtil {
	private static final String LANG_OPTION = "-l";
	private static final String EOL = File.separator;
	private static final String IMAGE_FORMAT = "jpg";

	public static String recognizeValidation(InputStream in) throws Exception {
		File tmpFile = File.createTempFile("img", "." + IMAGE_FORMAT);
		OutputStream out = new FileOutputStream(tmpFile);
		IOUtils.copy(in, out);
		IOUtils.closeQuietly(out);
		return format(recognizeText(tmpFile, IMAGE_FORMAT));
	}

	private static String format(String str) {
		if (str.isEmpty()) {
			return null;
		}
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String recognizeText(File imageFile, String imageFormat)
			throws Exception {
		File tempImage = createImage(imageFile, imageFormat);

		File outputFile = new File(imageFile.getParentFile(), "output");
		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();
		String s="C:/Program Files (x86)/Tesseract-OCR/";
		//cmd.add(SystemUtils.getUserDir() + "/tesseract/tesseract.exe");
		cmd.add(s+"tesseract.exe");
		cmd.add(s+"/tessdata/chi_sim");
		cmd.add(outputFile.getName());
		cmd.add(LANG_OPTION);
		cmd.add("chi_sim");
		cmd.add("eng");
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(imageFile.getParentFile());

		cmd.set(1, tempImage.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		int w = process.waitFor();

		// delete temp working files
		tempImage.delete();

		if (w == 0) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(outputFile.getAbsolutePath() + ".txt"),
					"utf-8"));

			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();
		} else {
			String msg;
			switch (w) {
			case 1:
				msg = "Errors accessing files. There may be spaces in your image's filename.";
				break;
			case 29:
				msg = "Cannot recognize the image or its selected region.";
				break;
			case 31:
				msg = "Unsupported image format.";
				break;
			default:
				msg = "Errors occurred.";
			}
			tempImage.delete();
			throw new RuntimeException(msg);
		}

		new File(outputFile.getAbsolutePath() + ".txt").delete();
		// logger.info("图像识别结果:" + strB);
		
		return strB.toString();

	}

	public static File createImage(File imageFile, String imageFormat) {
		File tempFile = null;
		try {
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersByFormatName(imageFormat);
			ImageReader reader = readers.next();

			ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
			reader.setInput(iis);
			// Read the stream metadata
			IIOMetadata streamMetadata = reader.getStreamMetadata();

			// Set up the writeParam
			TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(
					Locale.US);
			tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

			// Get tif writer and set output to file
			Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName("tiff");
			ImageWriter writer = writers.next();

			BufferedImage bi = reader.read(0);
			// bi = new ImageFilter(bi).changeGrey();
			IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(0));
			tempFile = tempImageFile(imageFile);
			ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
			writer.setOutput(ios);
			writer.write(streamMetadata, image, tiffWriteParam);
			ios.close();

			writer.dispose();
			reader.dispose();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return tempFile;
	}

	private static File tempImageFile(File imageFile) {
		String path = imageFile.getPath();
		StringBuffer strB = new StringBuffer(path);
		strB.insert(path.lastIndexOf('.'), 0);
		return new File(strB.toString().replaceFirst("(?<=\\.)(\\w+)$", "tif"));
	}

	public static void main(String[] args) throws Exception {
//		String maybe = recognizeValidation(new URL(
//				"http://passport.360buy.com/ImageVerifier.axd?uid=c360a45f-02b2-4255-8f2e-61191bfc3866")
//				.openStream());
		String maybe2 = new OCRUtil()
				.recognizeText(new File("E:\\sample-images\\11\\11.jpg"), "jpg");
		System.out.println(maybe2);
	}
}
