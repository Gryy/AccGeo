package Tools;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class mainTest {
	
	public static void main(String args[]) {
	
	        //compute 2^-128
		System.out.println(Math.pow(2, -128));
		
            //test AES
	         AESUtils aesUtils = new AESUtils();
	        String[] keys = {  
	                     "35 115"//, "39.513574 116.928153"
	         };  
	         System.out.println("key | AESEncode | AESDecode");  
	         for (String key : keys) {  
	             System.out.print("Key:"+key + " | ");
	                 
	             long tim1 = System.currentTimeMillis();
	             String encryptString = aesUtils.AESEncode(key,"abc");
	             String encryptString1 = aesUtils.AESEncode(key,"abc");
	             System.out.print("加密后的字符串是："+encryptString1+"\n");
	             long tim2 = System.currentTimeMillis();
	             double timeOfEncrypt = tim2 - tim1;
	             System.out.print("加密后的字符串是：" + encryptString + " | " + "加密所需要的时间：" + timeOfEncrypt); 
	                 
	             long tim3 = System.currentTimeMillis();
	             String decryptString = aesUtils.AESDecode(encryptString,"abc");  
	             long tim4 = System.currentTimeMillis();
	             double timeOfDecrypt = tim4 - tim3;
	             System.out.print("解密后的字符串是：" + decryptString + " | " + "解密所需要的时间：" + timeOfEncrypt + "\n"); 

	        }  
	              	   
          
   }
}