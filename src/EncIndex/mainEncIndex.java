package EncIndex;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.TypeKind;

import Tools.FileReadWrite;

public class mainEncIndex {
	
	
	
	
	public static void main(String args[]) throws Exception {
		
		String fOUT = "/Users/Grykie/Documents/workspace/HyCo/src/dataOut/RecordEnc.txt"; 
		
		FileReadWrite fOperation = new FileReadWrite();
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(15); 
        nf.setRoundingMode(RoundingMode.DOWN);

		/**@Step-1 Build the encrypted index
		 * @Output file2: dataOut.Server/cipherIndex.txt
		 * @author Rykie
		 * */
        int f=20;
        int t=7;
        int lenByte_key =16;
        int lenByte_id =5;
        int lenH =256;
		EncIndexByClient eIndex = new EncIndexByClient("ysdgsdhakdjsad",f,t,lenByte_key,lenByte_id,lenH);	//lamda=128 so16; id-Bytes5; bits-length256
		
	
		
		int dataSize = eIndex.outputFile_initCode();
		                     long timEI1= System.currentTimeMillis();
		eIndex.outFiles_buildEncIndex();
		                     long timEI2= System.currentTimeMillis();

		

		
		                     //Part5th: Size of encIndexes (size of inx+ dataset size)   (Byte)
			                    double sizeINX = lenH/8.0*dataSize/1024.0;   
			                  //Part6th: Size of local list (size of A_loc)   (Byte)
			                    double sizeA_loc = (t-1)*f/8*dataSize/1024.0;
			                   String record1 = "2.Index Cost (1) The Flag bits length is : "+f+" ;\n"
			                   		+ "The size of encrypted indexes is : "+ nf.format(sizeINX) +"KB." 
			                		   +"\n(2) The size of local list is : " + nf.format(sizeA_loc) + "KB."
			                				  + "\n(3) The encryption time on INXes is : "+ nf.format(timEI2-timEI1) +"ms.";
			                   
			                   fOperation.StringToFile(fOUT, 
			        		    		"\n\n\n 1. Dataset Info \n(1) The current dataset is "+dataSize +" tuples ;\n"			     
			        		    						+ "(2) The length of a geohash code in Dataset : t ="+t+" \n");
			                   
			                   fOperation.StringToFile(fOUT, record1);
  
	}

}
