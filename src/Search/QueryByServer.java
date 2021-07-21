package Search;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import Tools.FileReadWrite;

public class QueryByServer {	
	
	public static String f2="/Users/Grykie/Documents/workspace/HyCo/src/dataOut/Server/cipherIndex.txt";
	/*
	 * @input: token and file:cipherIndex.txt*/
	public static List<String> query_Server(int f, int len_H, int tq, List<BitSet> token) {
		
		
		//1.read list-cipherEntries from file f2 to inxLIST(List<BitSet>)
		FileReadWrite fOperation = new FileReadWrite();
		//Read List LT from file4
		List<String> content = new ArrayList<String>();
		content = fOperation.readFileToStringList(f2);
		Iterator<String> it = content.iterator();
		List<BitSet> INX = new ArrayList<BitSet>();
		List<String> ciP = new ArrayList<String>();
		while (it.hasNext()) {
			String line = it.next();
			String[] eachstr =line.split("[,\\s]+");
			int lenEstr = eachstr.length;
			if(lenEstr!=1){
				BitSet inx = new BitSet(len_H);
				//set 0st the head one
				String Hstr = eachstr[0].substring(1);
				inx.set(Integer.parseInt(Hstr));	
				for (int i=1; i<lenEstr-2;i++){
					inx.set(Integer.parseInt(eachstr[i]));
				}
				//set the last one
				String str = eachstr[lenEstr-2];
				String Lstr =  str.substring(0,  str.length()-1);
				inx.set(Integer.parseInt(Lstr));
			    INX.add(inx);
			            // System.out.println(inx+" "+eachstr[lenEstr-1]+"\n");
			    ciP.add(eachstr[lenEstr-1]);
			}
		}
		
		
		//2. use TK(List<BitSet>) to query INX(List<BitSet>)
		List<String> ciR = new ArrayList<String>();
		if (token.size()!=INX.size()){
			          System.out.println("Server: token is with error size!");
		}else {
			for (int i=0; i<INX.size();i++){
				BitSet curR = (BitSet) token.get(i).clone();
				curR.xor(INX.get(i));
				BitSet flagBT = (BitSet) curR.get((tq-1)*f,tq*f).clone();
				                       //System.out.println("flag: "+flagBT);
				if(flagBT.isEmpty()){
					                  //System.out.println("id: "+i+" is queried! \n");
					ciR.add(ciP.get(i));
				}
			}
		}
		
		
	    return ciR;
	
	
	}

	
public static List<String> query_ServerByOptToken(int f, int len_H, int tq, List<BitSet> optToken) {
	
	
	//1.read list-cipherEntries from file f2 to inxLIST(List<BitSet>)
	FileReadWrite fOperation = new FileReadWrite();
	//Read List LT from file4
	List<String> content = new ArrayList<String>();
	content = fOperation.readFileToStringList(f2);
	Iterator<String> it = content.iterator();
	List<BitSet> INX = new ArrayList<BitSet>();
	List<String> ciP = new ArrayList<String>();
	while (it.hasNext()) {
		String line = it.next();
		String[] eachstr =line.split("[,\\s]+");
		int lenEstr = eachstr.length;
		if(lenEstr!=1){
			BitSet inx = new BitSet(len_H);
			//set 0st the head one
			String Hstr = eachstr[0].substring(1);
			inx.set(Integer.parseInt(Hstr));	
			for (int i=1; i<lenEstr-2;i++){
				inx.set(Integer.parseInt(eachstr[i]));
			}
			//set the last one
			String str = eachstr[lenEstr-2];
			String Lstr =  str.substring(0,  str.length()-1);
			inx.set(Integer.parseInt(Lstr));
		    INX.add(inx);
		            // System.out.println(inx+" "+eachstr[lenEstr-1]+"\n");
		    ciP.add(eachstr[lenEstr-1]);
		}
	}
	
	
	//2. use TK(List<BitSet>) to query INX(List<BitSet>)
	List<String> ciR = new ArrayList<String>();
	if (optToken.size()!=INX.size()){
		          System.out.println("Server: token is with error size!");
	}else {
		for (int i=0; i<INX.size();i++){
			BitSet curR = (BitSet) optToken.get(i).clone();
			
			curR.xor(INX.get(i).get(0, tq*f));
			BitSet flagBT = (BitSet) curR.get((tq-1)*f,tq*f).clone();
			                       //System.out.println("flag: "+flagBT);
			if(flagBT.isEmpty()){
				                  //System.out.println("id: "+i+" is queried! \n");
				ciR.add(ciP.get(i));
			}
		}
	}
	
	
    return ciR;


}
}
