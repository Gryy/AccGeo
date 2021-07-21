package EncIndex;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.AcceptPendingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.EncryptedPrivateKeyInfo;

import org.apache.bcel.util.ByteSequence;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import Tools.AESUtils;
import Tools.Bitset_ByteArray;
import Tools.FileReadWrite;
import Tools.GeoHash_etd;
import Tools.HexUtils;
import Tools.SecureHash;

public class EncIndexByClient {
	
	private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7',   
            '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',   
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}; 
	private static String sk_AES;
	public  static int f ;  
	public   static int t ; 
	public   static int lenByte_key ; //lamda=128
	public   static int lenByte_id ; //id-Bytes
	public  static int lenH; //bits-length
	private static Map<Character, byte[]> mSV = new HashMap<Character, byte[]>();
	private static String file0="/Users/Grykie/Documents/workspace/HyCo/src/dataIn/initdata.txt";
	private static String file1="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/initCode.txt";
	public static String file2="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Server/cipherIndex.txt";
	public static String file3="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/SV.txt";
	//public static String file4="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Client/LT.txt";
	
	public EncIndexByClient( String sk, int ff, int tt, int kke, int ii, int hh) throws Exception{
		sk_AES =sk;
		f= ff;
		t=tt;
		lenByte_key=kke;
		lenByte_id=ii;
		lenH=hh;
	}
	

	




	public static  void Gen_mapSV() {
		
		for(int i=0; i<32; i++){		
			byte[] value = new byte[lenByte_key];
			SecureRandom random = new SecureRandom();
			random.nextBytes(value);			
			mSV.put(CHARS[i], value);
			
		}
	//	return mSV;
	}
	
	
	/**@author Rykie
	 * @return
	 * File: initCode(Id-Lat-Lon-EachCode) 
	 * */
	public static int outputFile_initCode() {
				
       /* read File To List */
        FileReadWrite fOperation = new FileReadWrite();
		
		List<String> list = new ArrayList<String>();
		list = fOperation.readFileToStringList(file0);
	                 //System.out.println(list);
	
	    /* call function for getting geoHash Code */
		double lat,lon; 
		int i;
BufferedWriter out = null;
try {
out = new BufferedWriter(new OutputStreamWriter(
new FileOutputStream(file1, true)));
	    
		for (i=0;i< list.size(); i++) {
			String[] eachline =list.get(i).split("\\s+");
			 
			/* generate geoHash Code */
			 lat = Double.parseDouble(eachline[1]);
			 lon = Double.parseDouble(eachline[2]);
			 GeoHash_etd gH = new GeoHash_etd(lat,lon);
			 gH.sethashLength(t);
			 String geoHash_code = gH.getGeoHashBase32();
        			         			                
			 String strLine = eachline[0]+" "+eachline[1]+" "+eachline[2]+" "+geoHash_code;
			        //System.out.println(strLine);
			 
			/* write String to File */			
		    out.write(strLine+"\r\n");		    	
	}
		return i;
		
} catch (Exception e) {e.printStackTrace();}
finally {
try {
    out.close();
    } catch (IOException e) {e.printStackTrace(); } }
return 0;

}
	
	
	
	/**@Step-2 build the encrypted index, and send it to server
	 * @author Rykie
	 * compute \Gamma^{*}(P_{i})
	 * @return 
	 * @throws Exception 
	 * */
	public static void outFiles_buildEncIndex() throws Exception{
		
		Gen_mapSV();
		 
		 
		/*inxLIST: write to file
		  deltaLIST: to return */
		List<BitSet> inxLIST = new ArrayList<BitSet>(); 
		List<BitSet> deltaL = new ArrayList<BitSet>();//
	    Bitset_ByteArray bitByte = new Bitset_ByteArray();
		/*read file1 to codesLists */
		FileReadWrite fOperation = new FileReadWrite();
		
		List<String> content = new ArrayList<String>();
		content = fOperation.readFileToStringList(file1);
		Iterator<String> it = content.iterator();
		List<String> codesList = new ArrayList<String>();
		List<String> cirPList = new ArrayList<String>();
		while (it.hasNext()) {
			String line = it.next();
			String[] eachstr =line.split("\\s+");
		    codesList.add(eachstr[3]);
		    String curP = eachstr[1]+" "+eachstr[2];
		    cirPList.add(genEncryptedP(curP));
		}
		
		
		int  dsize = codesList.size();
		for (int j =0; j< codesList.size(); j++){
			
			String curCode=codesList.get(j);
			String curId = String.valueOf(j);
			
			
			SecureHash secureHash = new SecureHash();
			 BitSet EncInx = new BitSet();
			for (int g=1; g<=t; g++){		
				
				byte[] sv_g = mSV.get(curCode.charAt(g-1));
				byte[] BytcurId =curId.getBytes();
				int lenpreH = lenByte_key+lenByte_id+1;		
				byte[] preH = new byte[lenpreH]; //preH :preimage of H
				for (int i=0; i<lenByte_key;i++){
						preH[i]=sv_g[i];
				}
				for (int i=lenByte_key; i<lenByte_key+BytcurId.length;i++){
					preH[i]=BytcurId[i-lenByte_key];
				}
				preH[lenpreH-1]=(byte)g;
				
				 
				 /*compute H and current EncryptedInx on g-th of P_{id}*/
				 BitSet H = secureHash.getHash(preH);
				 BitSet curEncInx = new BitSet();
					for (int i=0; i<lenH; i++){
						curEncInx.set((g-1)*f+i, H.get(i));
					}
					curEncInx.set((g-1)*f+lenH, (t-1)*f+lenH, false);
					                 //System.out.println("the "+g+"-h INXcell:"+ curEncInx);
					EncInx.xor(curEncInx);
			}
			
			/*subEncInx is a single inxEntry*/
			BitSet subEncInx = (BitSet) EncInx.get(0,lenH).clone();
		                            //	System.out.println("INX before ran: "+subEncInx.get(120, 160));
			
			 /*generate \delta_{i} with bits lenH, 256*/
			byte[] value = new byte[lenH/8];
			SecureRandom random = new SecureRandom();
			random.nextBytes(value);				
			BitSet delta = Bitset_ByteArray.fromByteArray(value);
			                    //System.out.println("deltaLT:"+delta.get(120, 160));
			
		    /*get final \Gamma^{*}(P_{i}) masked by eta_{i} */
			subEncInx.xor(delta);

			                 //System.out.println("final INX: "+subEncInx.get(120, 160)+"\n");
			inxLIST.add(subEncInx);
			deltaL.add(delta);
		}
		
		
		/*write inxLIST to file2: cipherIndex.txt*/
			//List To String
			StringBuffer sbfList = new StringBuffer();
			Iterator<BitSet> it1 = inxLIST.iterator();
			Iterator<String> it2 = cirPList.iterator();
				while(it1.hasNext()) {
					it2.hasNext();
					sbfList.append(it1.next()).append(" ").append(it2.next()).append("\n");
				}
			
			fOperation.StringToFile(file2, sbfList.toString());
			
			/*write mapSV to file3*/
			StringBuffer sbfMap = new StringBuffer();
			Iterator<Character> it3 = mSV.keySet().iterator();
			while(it3.hasNext()){
				char ID = it3.next();
				byte[] svBte = mSV.get(ID);
				String svStr = HexUtils.bytes2Hex(svBte);
				
			   sbfMap.append(ID).append(" ").append(svStr).append("\n");
			}
			/*write deltaList to file3:SV.txt*/
			//List To String
			StringBuffer sbfDeltaL = new StringBuffer();
			sbfDeltaL.append(dsize).append("\n");
			Iterator<BitSet> it4 = deltaL.iterator();
				while(it4.hasNext()) {
					sbfDeltaL.append(it4.next()).append("\n");
				}
			fOperation.StringToFile(file3, sbfMap.toString()+sbfDeltaL.toString());
			
	
			
			
	}
	
	
	
	
    public static String genEncryptedP(String P) throws Exception{
		
		AESUtils aesUtils = new AESUtils();
		String EncP= aesUtils.AESEncode(P, sk_AES);
	
		return EncP;
	}

    

	



}
