package Update;

import java.math.RoundingMode;
import java.text.NumberFormat;

import Tools.FileReadWrite;

public class mainUpdate {
	
	
	public static void main(String args[]) throws Exception {
		
		String fOUT = "/Users/Grykie/Documents/workspace/HyCo/src/dataOut/RecordUpdate.txt"; 
		
		FileReadWrite fOperation = new FileReadWrite();
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(15); 
        nf.setRoundingMode(RoundingMode.DOWN);
		
		int f=20;
        int t=7;
        int lenByte_key =16;
        int lenByte_id =5;
        int lenH =256;
        int n = 2326;
        String sk = "ysdgsdhakdjsad";
       String file2="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Server/cipherIndex.txt";
       String file3="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/SV.txt";
        AddthenDel addthenDel = new AddthenDel(t, lenByte_key, lenByte_id, lenH, f, file3, file2, sk);
        
                 long tim1 = System.currentTimeMillis();
        addthenDel.UpdateSinPoint(n, 32.73180, -117.20191);
                 long tim1_ = System.currentTimeMillis(); 
                 
                 fOperation.StringToFile(fOUT, 
     		    		"\n\n\n1. Dataset Info \n(1) The current dataset is " + n+ " tuples. \n"
     		    				+ "(2) f = "+ f +" and t =" + t+" .\n"
     		    		+ "2. Update Analy \n A single update needs : "+nf.format(tim1_-tim1)+" ms.\n");		     
     		    						
		
	}

}
