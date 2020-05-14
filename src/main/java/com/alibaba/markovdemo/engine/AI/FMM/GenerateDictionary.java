package com.alibaba.markovdemo.engine.AI.FMM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class GenerateDictionary {
    public void genHashDic(String filename, HashMap hm, HashMap len)
            throws FileNotFoundException, IOException {
        String s=new String();
        BufferedReader in = new BufferedReader(
                new FileReader(filename));
        while((s=in.readLine())!=null)
        {
            hm.put(s,s.length());
            len.put(s.length(), s);
        }
    }


}
