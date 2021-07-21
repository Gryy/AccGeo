

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

public class  ParamsGenTokens {
	

	public  static int f ;  
	public   static int lenByte_key ; //lamda=128
	public   static int lenByte_id ; //id-Bytes
	public  static int lenH; //bits-length
	public static String file3;
	

	public ParamsGenTokens(int kke, int ii, int hh,  String ffile3, int ff) {
		// TODO Auto-generated constructor stub
		f= ff;
		lenByte_key=kke;
		lenByte_id=ii;
		lenH=hh;
		file3 =ffile3;
	}
}