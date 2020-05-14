package com.alibaba.markovdemo;

import com.alibaba.markovdemo.engine.AI.FMM.GenerateDictionary;
import com.alibaba.markovdemo.engine.AI.FMM.Segmentation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@SpringBootTest
class MarkovDemoApplicationTests {

	public static String dealDateFormat(String oldDate) {
		Date date1 = null;
		DateFormat df2 = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date date = df.parse(oldDate);
			SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
			date1 = df1.parse(date.toString());
			df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return df2.format(date1);
	}


	@Test
	void wordTest () throws IOException {

		// FileInputStream fis = new FileInputStream("199801.txt");
		// FileOutputStream fos = new FileOutputStream("dic.txt");
		// ImportCorpus readF = new ImportCorpus(fis, fos);
		// readF.readDic();
		// System.out.println("µ¼Èë½áÊø");

		String filename = "dic.txt";
		HashMap hm = new HashMap();
		HashMap len = new HashMap();
		GenerateDictionary genDic = new GenerateDictionary();
		Segmentation seg;

		genDic.genHashDic(filename, hm, len);

		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(reader);
		String data = "";
		data = br.readLine();

		seg = new Segmentation(hm, len);

		String FmmTarget = seg.Fmm(data);
		String BmmTarget = seg.Bmm(data);

		System.out.println("FmmTarget: " + FmmTarget);
		System.out.println("BmmTarget: " + BmmTarget);
	}

}
