package Tools;

import java.io.RandomAccessFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;  

import java.io.RandomAccessFile; 


public class FileRandomAccess {

 
	public static void removeEnd(String filepath)
			throws Exception {
 
		RandomAccessFile file = new RandomAccessFile(filepath, "rw");
		long length = file.length() - 1;
		byte b;
		do {                     
		  length -= 1;
		  file.seek(length);
		  b = file.readByte();
		} while(b != 10);
		file.setLength(length+1);
		file.close();
	
}

	
	 public static void changeFile(String filePath, String oldStr,String newStr){
	        try {
	            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
	            String line;
	            while (null!=(line=raf.readLine())) {
	            	String[] split = line.split("//s+");
	                if(split[0]==oldStr){	                    
	                    raf.seek(split[0].length());
	                    raf.writeBytes(newStr);
	                    raf.writeBytes(split[1]);
	                }
	            }
	           raf.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


}
