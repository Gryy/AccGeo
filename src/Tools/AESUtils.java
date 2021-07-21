package Tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;  
  

/**     
 * @Title: AESUtils.java   
 * @Package com.fendo.MD5   
 * @Description: TODO  
 * @author fendo  
 * @date 2017年9月11日 下午5:48:17   
 * @version V1.0     
*/  
public class AESUtils {  
  
     
    
    private static Base64 base64 = new Base64 ();
  
    /**  
     * 加密  
     * 1.构造密钥生成器  
     * 2.根据ecnodeRules规则初始化密钥生成器  
     * 3.产生密钥  
     * 4.创建和初始化密码器  
     * 5.内容加密  
     * 6.返回字符串  
     */  
    public static String AESEncode(String content, String encodeRules) {  
        try {  
            //1.构造密钥生成器，指定为AES算法,不区分大小写  
            KeyGenerator keygen = KeyGenerator.getInstance("AES");  
            //2.根据ecnodeRules规则初始化密钥生成器  
            //生成一个128位的随机源,根据传入的字节数组  
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");  
            random.setSeed(encodeRules.getBytes());  
            keygen.init(128, random);  
            //3.产生原始对称密钥  
            SecretKey original_key = keygen.generateKey();                   //key
            //4.获得原始对称密钥的字节数组  
            byte[] raw = original_key.getEncoded();  
            //5.根据字节数组生成AES密钥  
            SecretKey key = new SecretKeySpec(raw, "AES");  
            //6.根据指定算法AES自成密码器  
            Cipher cipher = Cipher.getInstance("AES");  
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY  
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码  
            byte[] byte_encode = content.getBytes("utf-8");  
            //9.根据密码器的初始化方式--加密：将数据加密  
            byte[] byte_AES = cipher.doFinal(byte_encode);  
            //10.将加密后的数据转换为字符串  
            String AES_encode = new String(base64.encode(byte_AES));  
            //11.将字符串返回  
            return AES_encode;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        //如果有错就返加nulll  
        return null;  
    }  
    
    public static String aesEncryptToString(String content, String encryptKey) throws Exception {    
        KeyGenerator kgen = KeyGenerator.getInstance("AES");    
        kgen.init(128, new SecureRandom(encryptKey.getBytes()));    
    
        Cipher cipher = Cipher.getInstance("AES");    
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES")); 
        
        String AES_encode = new String(base64.encode(cipher.doFinal(content.getBytes("utf-8")))); 
        
        return AES_encode;    
    } 
  
  
      
  
    /**   
     * AES加密   
     * @param content 待加密的内容   
     * @param encryptKey 加密密钥   
     * @return 加密后的byte[]   
     * @throws Exception   
     */    
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {    
        KeyGenerator kgen = KeyGenerator.getInstance("AES");    
        kgen.init(128, new SecureRandom(encryptKey.getBytes()));    
    
        Cipher cipher = Cipher.getInstance("AES");    
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));    
            
        return cipher.doFinal(content.getBytes("utf-8"));    
    }    
      
    /**  
     * 解密  
     * 解密过程：  
     * 1.同加密1-4步  
     * 2.将加密后的字符串反纺成byte[]数组  
     * 3.将加密内容解密  
     */  
    public static String AESDecode(String content, String encodeRules) {  
        try {  
            //1.构造密钥生成器，指定为AES算法,不区分大小写  
            KeyGenerator keygen = KeyGenerator.getInstance("AES");  
            //2.根据ecnodeRules规则初始化密钥生成器  
            //生成一个128位的随机源,根据传入的字节数组  
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");  
            random.setSeed(encodeRules.getBytes());  
            keygen.init(128, random);  
            //3.产生原始对称密钥  
            SecretKey original_key = keygen.generateKey();  
            //4.获得原始对称密钥的字节数组  
            byte[] raw = original_key.getEncoded();  
            //5.根据字节数组生成AES密钥  
            SecretKey key = new SecretKeySpec(raw, "AES");  
            //6.根据指定算法AES自成密码器  
            Cipher cipher = Cipher.getInstance("AES");  
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY  
            cipher.init(Cipher.DECRYPT_MODE, key);  
            //8.将加密并编码后的内容解码成字节数组  
            byte[] byte_content = base64.decode(content);  
            /*  
             * 解密  
             */  
            byte[] byte_decode = cipher.doFinal(byte_content);  
            String AES_decode = new String(byte_decode, "utf-8");  
            return AES_decode;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        //如果有错就返加nulll  
        return null;  
    }  
      
      
    /**   
     * AES解密   
     * @param encryptBytes 待解密的byte[]   
     * @param decryptKey 解密密钥   
     * @return 解密后的String   
     * @throws Exception   
     */    
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {    
        KeyGenerator kgen = KeyGenerator.getInstance("AES");    
        kgen.init(128, new SecureRandom(decryptKey.getBytes()));    
            
        Cipher cipher = Cipher.getInstance("AES");    
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));    
        byte[] decryptBytes = cipher.doFinal(encryptBytes);    
            
        return new String(decryptBytes);    
    }    
      
  

    /**   
     * 将base 64 code AES解密   
     * @param encryptStr 待解密的base 64 code   
     * @param decryptKey 解密密钥   
     * @return 解密后的string   
     * @throws Exception   
     */    
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {    
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64.decode(encryptStr), decryptKey);    
    }    
      
    /**   
     * AES加密为base 64 code   
     * @param content 待加密的内容   
     * @param encryptKey 加密密钥   
     * @return 加密后的base 64 code   
     * @throws Exception   
     */    
    public static String aesEncrypt(String content, String encryptKey) throws Exception {    
        return base64.encode(aesEncryptToBytes(content, encryptKey));    
    }  
   
}  
