package Search;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.List;

import EncIndex.EncIndexByClient;
import EncIndex.ParamsGenTokens;
import Tools.FileReadWrite;

public class mainSearch {
	
	public static void main(String args[]) throws Exception {
		
		String fOUT = "/Users/Grykie/Documents/workspace/HyCo/src/dataOut/RecordQUERY.txt"; 
		
		FileReadWrite fOperation = new FileReadWrite();
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(15); 
        nf.setRoundingMode(RoundingMode.DOWN);
        
 
        //params for gen tokens
		int f =20;
		int lenByte_key =16;
		int lenByte_id =5;
		int lenH=256;
		String file3 = "/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/SV.txt";
		ParamsGenTokens paramsGenTokens = new ParamsGenTokens(lenByte_key, lenByte_id, lenH, file3, f); //16 and 5 are byte size for initing structure
		//params for Dec
		String sk_AES = "ysdgsdhakdjsad";  
		
		
	  
		
		/**@Step-1 Gentoken BY CLIENT
		   * @Output 
		   * @author Rykie
		 * */
	       // query params
			double radius=10000;
			int tQ=4;
			double lat=32.73180;
			double lon=-117.20191;	
	                      //Part3rd: Time For generating Tokens
	   GenTokenByClient gToken = new GenTokenByClient(paramsGenTokens,radius,tQ,lat,lon);  //radius(m), tq, lat, lon, need to use:
	                        long timTK1_ = System.currentTimeMillis();
	//     List<BitSet> TK =gToken.genTokenClient();  
	                        long timTK1 = System.currentTimeMillis();	                     	                        
	                        long timTK2_ = System.currentTimeMillis();     
	     List<BitSet> optTK =gToken.genOptTokenClient();
	                        long timTK2 = System.currentTimeMillis(); 
	                   /**    String record3 = "3. Token Time \n"
	                        		+ "(1) The time on generating all tokens of UNOPTIMIZED is : "+ nf.format(timTK1-timTK1_) + "ms."
	                                           +"\n(2) The time on generating all tokens of OPTIMIZED is : "+ nf.format(timTK2-timTK2_) + "ms.";
	                      **/  
	   
	   int dataSize = gToken.dSize;
	   /**@Step-2 SEARCH>>>>Query BY SERVER
		   * @Input TK, cipherIndex 
		   * @Output R(ciphertext Result)
		   * @author Rykie
		   * */
	                      //Part4th: Time for once Query ==in Server+in Client
		QueryByServer qServer = new QueryByServer();
		                    long timQS1_= System.currentTimeMillis();
     //  List<String> R =qServer.query_Server(f,lenH,tQ,TK); 
       List<String> R =qServer.query_ServerByOptToken(f,lenH,tQ,optTK);
		                    long timQS1= System.currentTimeMillis();
		                    double timQS=timQS1-timQS1_;
		                    
		                    
		                    		
		                    
		/**@Step-3 Decrypt>>>>Decrypt and Compute BY CLIENT
		   * @Input R(ciphertext Result)
		   * @Output queriedP
		   * @author Rykie
		   *      **/  
		DecCpareByClient dCBYClient =new DecCpareByClient();
		                    long timQC1= System.currentTimeMillis();
		int qNum=dCBYClient.decryptAndCompare(sk_AES, R, radius, lat, lon);
		                    long timQC2= System.currentTimeMillis();
		                    double timQC=timQC2-timQC1;
		                    String record2 = "2. The OPTIMIZED time of once range query is : Server--"+ nf.format(timQS) +"ms and " +
		        	     			"Client--"+nf.format(timQC) +"ms.\n"+
		        	     			"the OPTIMIZED total query time is :" +nf.format(timQS+timQC) +"ms.\n"+
		        	     			"the number of retrieved Points is : "+ qNum+".\n\n"; 
		                    
		                    

		        	     /** Size of token generation
		                   double sizeTKs = lenH*dataSize/8.0/1024.0;
		                   double sizeOptTKs = tQ*f*dataSize/8.0/1024.0;
		                   String record4 = "4. Token Overhead \n (1)The size of tokens is : " + nf.format(sizeTKs) + "KB."
		                		                + "\n (2)The size of OPTIMIZED tokens is : " + nf.format(sizeOptTKs) + "KB.\n\n"; **/
		        	     	
		        	     	
		                   fOperation.StringToFile(fOUT, 
		        		    		"\n\n 1. (1) The dataset size is : 1/4n =" + dataSize +". \n"
		        		    		+ "(2) The current query is from the point ("+lat+", "+lon+") with a distance of "+radius+"m. \n"
		        		    				+ "(3) The length of a search token : tQ ="+tQ + ". \n"
		        		    				               + "(4) The Flag bits length : f =" + f + " .");
		        			                            
		        	     	fOperation.StringToFile(fOUT, record2); 
		        	     //	fOperation.StringToFile(fOUT, record3);
		        	     //	fOperation.StringToFile(fOUT, record4);
		        	      
	   
	}

}
