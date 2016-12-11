package com.ansj.vec;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.ansj.vec.domain.WordEntry;
import com.jackie.crawler.doubanmovie.constants.Constants;
import com.jackie.crawler.doubanmovie.utils.TokenizerAnalyzerUtils;
import org.apache.commons.io.FileUtils;

import static com.jackie.crawler.doubanmovie.constants.Constants.ENCODING;

public class Word2VEC {

	public static void dataProcess() {
		File commentFile = new File(Word2VEC.class.getResource("/library/comment12031715.txt").getPath());
		long start = System.currentTimeMillis();
		try {
			StringBuilder vectorSB = new StringBuilder();
			List<String> lineList = FileUtils.readLines(commentFile, ENCODING);
			for (String line : lineList) {
				vectorSB.append(TokenizerAnalyzerUtils.getAnalyzerResult(line.trim()) + "\r\n");
				System.out.println("Parsing comment: " + line);
			}

			File file = new File("src/main/resource/library/comment/tokenizerResult.txt");
			List<StringBuilder> list = new ArrayList<StringBuilder>();
			list.add(vectorSB);
			FileUtils.writeLines(file, list);
			System.out.println("tokenizer analyze done!!!");
			System.out.println("process last: " + (System.currentTimeMillis() - start)/1000 + "s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {


		//preprocess the original comment to tokenizer and save as tokenizerResult.txt
//		dataProcess();


		//train the model and save model
		Learn learn = new Learn();
		learn.learnFile(new File("src/main/resource/library/comment/tokenizerResult.txt"));
		learn.saveModel(new File("src/main/resource/library/comment/vector.mod"));

		//use the trained model to analyze
		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel("src/main/resource/library/comment/vector.mod");
//
		System.out.println("电影" + "\t" +
		Arrays.toString(vec.getWordVector("电影")));

		String str = "反感";
		for (int i = 0; i < 20; i++) {
			System.out.println(vec.distance(str));
			;
		}

		List<String> wordList = new ArrayList<String>();
		wordList.add("烂片");
		wordList.add("画面");
		wordList.add("星");
		wordList.add("幽默");
		for (String word : wordList) {
			System.out.println(word + "\t" +
					vec.distance(word));
		}

		System.out.println(vec.analogy("男主角", "女主角", "演技"));
	}

	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private int words;
	private int size;
	private int topNSize = 40;

	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws java.io.IOException
	 */
	public void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}

				wordMap.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws java.io.IOException
	 */
	public void loadJavaModel(String path) throws IOException {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
		try {
			words = dis.readInt();
			size = dis.readInt();

			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				key = dis.readUTF();
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					value[j] /= len;
				}
				wordMap.put(key, value);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 近义词
	 * 
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);
		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	public Set<WordEntry> distance(List<String> words) {

		float[] center = null;
		for (String word : words) {
			center = sum(center, wordMap.get(word));
		}

		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	private float[] sum(float[] center, float[] fs) {
		// TODO Auto-generated method stub

		if (center == null && fs == null) {
			return null;
		}

		if (fs == null) {
			return center;
		}

		if (center == null) {
			return fs;
		}

		for (int i = 0; i < fs.length; i++) {
			center[i] += fs[i];
		}

		return center;
	}

	/**
	 * 得到词向量
	 * 
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 * 
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 * 
	 * @param dis
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

}
