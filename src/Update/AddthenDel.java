package Update;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xpath.operations.And;

import Tools.AESUtils;
import Tools.Bitset_ByteArray;
import Tools.FileRandomAccess;
import Tools.FileReadWrite;
import Tools.GeoHash_etd;
import Tools.HexUtils;
import Tools.SecureHash;

public class AddthenDel {

   private static int t;
   private static int lenByte_key;
   private static int lenByte_id;
   private static int lenH;
   private static int f;
   private static String file3;
   private static String file2;
   private static String sk_AES;

	// read mSV， cipherinx And cipherDat Local: SV
	public AddthenDel( int tt, int bbkey, int bbid, int llenh, int ff, String ffile3, String ffile2, String sk){
		t=tt;
		lenByte_key=bbkey;
		lenByte_id=bbid;
		lenH=llenh;
		f=ff;
		file3 =ffile3;
		file2 =ffile2;
		sk_AES = sk;
	}
	
    public static String genEncryptedP(String P) throws Exception{
		
		AESUtils aesUtils = new AESUtils();
		String EncP= aesUtils.AESEncode(P, sk_AES);
	
		return EncP;
	}
	
    /*add then delete*/
	public static void UpdateSinPoint(int n, Double lat, Double lon) throws Exception{
						
		/*1. read mSV from file3: SV.txt*/
		FileReadWrite fOperation = new FileReadWrite();
		List<String> content = new ArrayList<String>();
		content = fOperation.readFileToStringList(file3);
		Iterator<String> it = content.iterator();
		Map<Character, byte[]> mSV = new HashMap<Character, byte[]>();
		int line_id=0;
		while (line_id<32) {
			line_id++;
			String line = it.next();
			String[] eachstr =line.split("\\s+");
			if(eachstr.length!=1){
				String readsv = eachstr[1];
				byte[] svalue =HexUtils.hex2Bytes(readsv);
				if (svalue!=null){mSV.put(eachstr[0].charAt(0), svalue);
			}
			}			    
		}	
		
		/*2. get the cipher point :  encP*/
		String curP = lat+" "+lon;
	    String encP = genEncryptedP(curP);
		
		
		/*3. get encrypted inxEntry: subEncInx
		 *   local delta, that is a single item of A_loc : delta */
		 GeoHash_etd gH = new GeoHash_etd(lat,lon);
		 gH.sethashLength(t);
		 String curCode = gH.getGeoHashBase32();
		String curId = String.valueOf(n+1);
		
		
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
			
			 
			 //compute H and current EncryptedInx on g-th of P_{id}
			 BitSet H = secureHash.getHash(preH);
			 BitSet curEncInx = new BitSet();
				for (int i=0; i<lenH; i++){
					curEncInx.set((g-1)*f+i, H.get(i));
				}
				curEncInx.set((g-1)*f+lenH, (t-1)*f+lenH, false);
				                
				EncInx.xor(curEncInx);
		}
		
		//subEncInx is a single inxEntry
		BitSet subEncInx = (BitSet) EncInx.get(0,lenH).clone();
	                           
		
		 //generate \delta_{i} with bits lenH, 256
		byte[] value = new byte[lenH/8];
		SecureRandom random = new SecureRandom();
		random.nextBytes(value);				
		BitSet delta = Bitset_ByteArray.fromByteArray(value);
		                   
		
	    //get final \Gamma^{*}(P_{i}) masked by eta_{i} 
		subEncInx.xor(delta);

		/*4. wirte the encrypted inxEntry into file2: put subEncInx into cipherIndex.txt
		 *   write the pseudo into local file3: put delta into SV.txt */
		fOperation.StringToFile(file2, (subEncInx+" "+encP).toString());
		fOperation.StringToFile(file3, delta.toString());
		
		/*5. remove the encrypted inxEntry into file2: put subEncInx into cipherIndex.txt
		 *   remove the pseudo into local file3: put delta into SV.txt */
		FileRandomAccess rAccess = new FileRandomAccess();
	        rAccess.removeEnd(file2);
	        rAccess.removeEnd(file3);
		
	}
}
