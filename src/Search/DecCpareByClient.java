package Search;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import Tools.AESUtils;
import Tools.FileReadWrite;
import Tools.MapUtils;

public class DecCpareByClient {
	
	public static String resultFile ="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/queriedPoints.txt";
	
	
	public static int decryptAndCompare(String sk_AES, List<String> resultCipherPoint, double radius, double targetLat, double targetLon){
		List<String> PlainPoint = new ArrayList<String>();
		List<String> resultPlainPoint = new ArrayList<String>();
	
		
		//decrypt
		AESUtils aesUtils = new AESUtils();
		for(int i = 0; i < resultCipherPoint.size(); i ++ ) {
			
			PlainPoint.add(aesUtils.AESDecode(resultCipherPoint.get(i),sk_AES));			
		}
		
		MapUtils mapUtils = new MapUtils();
		
		//compare
		for(int j = 0; j < PlainPoint.size(); j ++) {
			
			String str = PlainPoint.get(j);
			String[] subStr = str.split("\\s+");
			
			double distance = mapUtils.GetDistance(Double.parseDouble(subStr[0]), Double.parseDouble(subStr[1]), targetLat, targetLon);
			
			if(distance < radius) {
				String latlon = subStr[0]+" "+subStr[1];
				resultPlainPoint.add(latlon);
			}
		}
		
		//output
		FileReadWrite fOperation = new FileReadWrite();
		
		if(!resultPlainPoint.isEmpty()) {
			
			//write to file 
BufferedWriter out = null;
try {
out = new BufferedWriter(new OutputStreamWriter(
new FileOutputStream(resultFile, true)));
			
			for(int k = 0; k < resultPlainPoint.size(); k ++ ) {
				
				out.write(resultPlainPoint.get(k)+"\r\n");		
				//fOperation.StringToFile(resultFile, resultPlainPoint.get(k));			
			}
} catch (Exception e) {e.printStackTrace();}
finally {
try {
    out.close();
    } catch (IOException e) {e.printStackTrace(); } }

			    //System.out.println(resultPlainPoint);
		}else {
			fOperation.StringToFile(resultFile, "there is no tuples in this Search Range!");
			    //System.out.println("there is no tuples in this Search Range!");
		}
		
		return resultPlainPoint.size();
	}

}
