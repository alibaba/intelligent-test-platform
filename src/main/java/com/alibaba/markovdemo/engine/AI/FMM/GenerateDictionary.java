package com.alibaba.markovdemo.engine.AI.FMM;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GenerateDictionary {

    public static void genHashDic(String filename, HashMap hm, HashMap len)
            throws FileNotFoundException, IOException {
        String s;
        BufferedReader in = new BufferedReader(
                new FileReader(filename));
        while ((s = in.readLine()) != null) {
            hm.put(s, s.length());
            len.put(s.length(), s);
        }
    }

    public static void genHashDic(URL url, HashMap<String, Integer> hm, HashMap<Integer, String> len) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        List<String> terms = IOUtils.readLines(in);
        for (String term : terms) {
            hm.put(term, term.length());
            len.put(term.length(), term);
        }


    }

}
