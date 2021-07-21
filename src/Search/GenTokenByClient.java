package Search;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.xpath.operations.Lt;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import EncIndex.EncIndexByClient;
import EncIndex.ParamsGenTokens;
import Tools.Bitset_ByteArray;
import Tools.FileReadWrite;
import Tools.HexUtils;
import Tools.SecureHash;

public class GenTokenByClient {
	
	private static double radius;
	public static int tQ;
	private static double lat;
	private static double lon;
	private static int len_BteKey;
	private static int len_BteId;
	private static int len_H;
	private static int f;
	private static String file3;
	public static int dSize;
	
	 public GenTokenByClient(ParamsGenTokens ppTokens, double rr, int tqq, double l1, double l2){
		 radius = rr;
		 tQ = tqq;
		 lat = l1;
		 lon = l2;
		 len_BteId = ppTokens.lenByte_id;
		 len_BteKey =ppTokens.lenByte_key;
		 len_H=ppTokens.lenH;
		 f=ppTokens.f;
		 file3=ppTokens.file3;
	 }
	
	public static List<BitSet> genTokenClient() throws Exception {
	
		 /* Read map SV and LT from file3 ,
		  * */
			FileReadWrite fOperation = new FileReadWrite();
			//Read map SV from file3
			List<String> content = new ArrayList<String>();
			content = fOperation.readFileToStringList(file3);
			Iterator<String> it = content.iterator();
			Map<Character, byte[]> SV = new HashMap<Character, byte[]>();
			int line_id=0;
			while (line_id<32) {
				line_id++;
				String line = it.next();
				String[] eachstr =line.split("\\s+");
				if(eachstr.length!=1){
					String readsv = eachstr[1];
					byte[] sv =HexUtils.hex2Bytes(readsv);
					if (sv!=null){SV.put(eachstr[0].charAt(0), sv);
				}
				}			    
			}		
			//Read List LT from file3
			List<BitSet> LT = new ArrayList<BitSet>();
			 dSize =Integer.parseInt(it.next());
			while (it.hasNext()) {
				String line = it.next();
				String[] eachstr =line.split(",\\s+");
				if(eachstr.length!=1){
					BitSet delta = new BitSet(len_H);
					//set 0st the head one
					String Hstr = eachstr[0].substring(1);
					delta.set(Integer.parseInt(Hstr));
					for (int i=1; i<eachstr.length-1;i++){
						delta.set(Integer.parseInt(eachstr[i]));
					}
					//set the last one
					String str = eachstr[eachstr.length-1];
					String Lstr =  str.substring(0,  str.length()-1);
					delta.set(Integer.parseInt(Lstr));
					//System.out.println(delta);
				    LT.add(delta);
				}
			}
		
		
		//tkLIST: generate a token  
		List<BitSet> tkLIST = new ArrayList<BitSet>();
		
		
		Tools.GeoHash_etd geo = new Tools.GeoHash_etd(lat,lon);
        geo.sethashLength(tQ);
        String curCode = geo.getGeoHashBase32();
      //  System.out.println(curCode);

		for (int j =0; j< dSize; j++){
			
			String curId = String.valueOf(j);
			
			SecureHash secureHash = new SecureHash();
			 BitSet entry = new BitSet();
			for (int g=1; g<=tQ; g++){
				
				byte[] sv_g = SV.get(curCode.charAt(g-1));
				byte[] BytcurId =curId.getBytes();
				int lenpreH = len_BteKey+len_BteId+1;		
				byte[] preH = new byte[lenpreH]; //preH :preimage of H
				for (int i=0; i<len_BteKey;i++){
						preH[i]=sv_g[i];
				}
				for (int i=len_BteKey; i<len_BteKey+BytcurId.length;i++){
					preH[i]=BytcurId[i-len_BteKey];
				}
				preH[lenpreH-1]=(byte)g;
				
				/*compute H and current EncryptedInx on g-th of P_{id}*/
				 BitSet H = secureHash.getHash(preH);
				 BitSet curCell = new BitSet();
					for (int i=0; i<len_H; i++){
						curCell.set((g-1)*f+i, H.get(i));
					}
					curCell.set((g-1)*f+len_H, (tQ-1)*f+len_H, false);
					entry.xor(curCell);
					                //System.out.println("the "+g+"-th cell: "+curCell);
			}
			
			BitSet oriTK = (BitSet) entry.get(0,len_H).clone();
			                        //System.out.println("\n TK before ran: "+subTK.get(120,160));
			
			 /*1--get \eta1_{i}
			  * 2--\delta_{i}
			  * 3--\eta2_{i}*/
			byte[] value1 = new byte[(tQ-1)*f/8];
			SecureRandom random1 = new SecureRandom();
			random1.nextBytes(value1);				
			BitSet eta1 = Bitset_ByteArray.fromByteArray(value1);
			
			BitSet curSub_delta = (BitSet) LT.get(j).get((tQ-1)*f, tQ*f).clone();
			byte[] value2 = new byte[(int) Math.ceil((len_H-tQ*f)/8-0.0001)];
			SecureRandom random2 = new SecureRandom();
			random2.nextBytes(value2);				
			BitSet eta20 = Bitset_ByteArray.fromByteArray(value2);
			BitSet eta2=(BitSet) eta20.get(0, len_H-tQ*f).clone();	
			
			
			
			 /*generate \eta1_{i}||\delta_{i}||-\eta2_{i}*/
			BitSet ranTK=new BitSet();
			for(int i=0; i<(tQ-1)*f; i++){
				ranTK.set(i,eta1.get(i));
			}
			for(int i= (tQ-1)*f; i<tQ*f; i++){
				ranTK.set(i,curSub_delta.get(i-(tQ-1)*f));
			}
			for(int i= tQ*f; i<len_H; i++){
				ranTK.set(i,eta2.get(i-tQ*f));
			}
			                         //System.out.println("ranTK: "+ranTK.get(120,160));
			
			oriTK.xor(ranTK);
			                          //System.out.println("The final TK, the "+j+"-th entryTK :"+subTK.get(120,160));
			tkLIST.add(oriTK);
		
		
		} 
		
		return tkLIST;
	}
	
	
	public static List<BitSet> genOptTokenClient() throws Exception {
		
		 /* Read map SV and LT from file3 ,
		  * */
			FileReadWrite fOperation = new FileReadWrite();
			//Read map SV from file3
			List<String> content = new ArrayList<String>();
			content = fOperation.readFileToStringList(file3);
			Iterator<String> it = content.iterator();
			Map<Character, byte[]> SV = new HashMap<Character, byte[]>();
			int line_id=0;
			while (line_id<32) {
				line_id++;
				String line = it.next();
				String[] eachstr =line.split("\\s+");
				if(eachstr.length!=1){
					String readsv = eachstr[1];
					byte[] sv =HexUtils.hex2Bytes(readsv);
					if (sv!=null){SV.put(eachstr[0].charAt(0), sv);
				}
				}			    
			}		
			//Read List LT from file3
			List<BitSet> LT = new ArrayList<BitSet>();
			 dSize =Integer.parseInt(it.next());
			while (it.hasNext()) {
				String line = it.next();
				String[] eachstr =line.split(",\\s+");
				if(eachstr.length!=1){
					BitSet delta = new BitSet(len_H);
					//set 0st the head one
					String Hstr = eachstr[0].substring(1);
					delta.set(Integer.parseInt(Hstr));
					for (int i=1; i<eachstr.length-1;i++){
						delta.set(Integer.parseInt(eachstr[i]));
					}
					//set the last one
					String str = eachstr[eachstr.length-1];
					String Lstr =  str.substring(0,  str.length()-1);
					delta.set(Integer.parseInt(Lstr));
					//System.out.println(delta);
				    LT.add(delta);
				}
			}
		
		
		//tkLIST: generate a token  
		List<BitSet> opttkLIST = new ArrayList<BitSet>();
		
		
		Tools.GeoHash_etd geo = new Tools.GeoHash_etd(lat,lon);
       geo.sethashLength(tQ);
       String curCode = geo.getGeoHashBase32();
     //  System.out.println(curCode);

		for (int j =0; j< dSize; j++){
			
			String curId = String.valueOf(j);
			
			SecureHash secureHash = new SecureHash();
			 BitSet entry = new BitSet();
			for (int g=1; g<=tQ; g++){
				
				byte[] sv_g = SV.get(curCode.charAt(g-1));
				byte[] BytcurId =curId.getBytes();
				int lenpreH = len_BteKey+len_BteId+1;		
				byte[] preH = new byte[lenpreH]; //preH :preimage of H
				for (int i=0; i<len_BteKey;i++){
						preH[i]=sv_g[i];
				}
				for (int i=len_BteKey; i<len_BteKey+BytcurId.length;i++){
					preH[i]=BytcurId[i-len_BteKey];
				}
				preH[lenpreH-1]=(byte)g;
				
				/*compute H and current EncryptedInx on g-th of P_{id}*/
				 BitSet H = secureHash.getHash(preH);
				 BitSet curCell = new BitSet();
					for (int i=0; i<len_H; i++){
						curCell.set((g-1)*f+i, H.get(i));
					}
					curCell.set((g-1)*f+len_H, (tQ-1)*f+len_H, false);
					entry.xor(curCell);
					                //System.out.println("the "+g+"-th cell: "+curCell);
			}
			
			BitSet optTK = (BitSet) entry.get(0,tQ*f).clone();
			                        //System.out.println("\n TK before ran: "+subTK.get(120,160));
			
			 /*1--get \eta1_{i}
			  * 2--\delta_{i}
			  * 3--\eta2_{i}*/
			byte[] value1 = new byte[(tQ-1)*f/8];
			SecureRandom random1 = new SecureRandom();
			random1.nextBytes(value1);				
			BitSet eta1 = Bitset_ByteArray.fromByteArray(value1);
			
			BitSet curSub_delta = (BitSet) LT.get(j).get((tQ-1)*f, tQ*f).clone();
			
			
			
			 /*generate \eta1_{i}||\delta_{i}||-\eta2_{i}*/
			BitSet ranTK=new BitSet();
			for(int i=0; i<(tQ-1)*f; i++){
				ranTK.set(i,eta1.get(i));
			}
			for(int i= (tQ-1)*f; i<tQ*f; i++){
				ranTK.set(i,curSub_delta.get(i-(tQ-1)*f));
			}

			                     
			
			optTK.xor(ranTK);
			                          //System.out.println("The final TK, the "+j+"-th entryTK :"+subTK.get(120,160));
			opttkLIST.add(optTK);
		
		
		} 
		
		return opttkLIST;
	}

	


}
