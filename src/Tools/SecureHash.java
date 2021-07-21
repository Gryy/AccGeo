package Tools;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

import javax.management.loading.PrivateClassLoader;

public class SecureHash {
	
	public static final String HASH = "SHA-256"; 
	


	public BitSet getHash(byte[] Input) throws NoSuchAlgorithmException {
		
		BigInteger sha = null;
		
		MessageDigest messageDigest = MessageDigest.getInstance(HASH);
		messageDigest.update(Input);
        sha = new BigInteger(messageDigest.digest()); 
		 	  

        
		BitSet output = BitSet.valueOf(sha.toByteArray());
	//	System.out.print(output.toString()+" ");
        
        
		return output;
		
	}
	
}
