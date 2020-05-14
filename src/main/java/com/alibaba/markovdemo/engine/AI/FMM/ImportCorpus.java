package com.alibaba.markovdemo.engine.AI.FMM;
import java.io.*;

public class ImportCorpus {
	private FileInputStream fis;
	private FileOutputStream fos;
	private int data;
	private int flag;
	
	public ImportCorpus(FileInputStream fis, FileOutputStream fos) {
		this.fis = fis;
		this.fos = fos;
		flag = 0;
	}
	public void readDic() throws IOException{
		    while((data=fis.read())!=-1)
		    	{
		    	if(data!=47&&data!=32&&flag==0){
		    	setDic();
		    	}
		       if(data==47){
				fos.write(0x0D);
		        flag = 1;
		        }
		       else {
				   flag = 0;
			   }
		    	}
		    fis.close();
	}
	public void setDic() throws IOException{
			fos.write(data);
	}
}
