package Tools;

import java.util.BitSet;

public class Bitset_ByteArray {

	public static BitSet fromByteArray(byte[] bytes) {
	    BitSet bits = new BitSet();

	    int i=0;
	    for (i=0; i<bytes.length*8; i++) {
	        if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	            bits.set(i);
	        }
	    }    
	//    System.out.println(i);
	   
	    return bits;
	}

	public static byte[] toByteArray(BitSet bits) {
		
	    byte[] bytes = new byte[bits.length()+1];
	    
	    for (int i=0; i<bits.length(); i++) {
	        if (bits.get(i)) {
	            bytes[bits.length()-i/8-1] |= 1<<(i%8);
	        //    System.out.print("bytes.length-i/8-1:"+tt+" ");
	        }
	    }
	    return bytes;
	}
	
}
