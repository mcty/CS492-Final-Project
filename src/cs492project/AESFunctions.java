package cs492project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * File:
 * AESFunctions.java 
 * 
 * Purpose:
 * This file  is used for AES encryption, and AES decryption with some of 
 * RSAFunctions.
 * 
 * References for documentation:
 * - This source helped learn RSA
 * - https://www.novixys.com/blog/using-aes-rsa-file-encryption-decryption-java/
 * 
 * - This source helped learn to check if files exist
 * - https://alvinalexander.com/java/java-file-exists-directory-exists/
 * - https://stackoverflow.com/questions/17939556/how-to-get-the-execution-directory-path-in-java
 * 
 * @author tts-macbook
 */

public class AESFunctions {
    
    static SecureRandom srandom = new SecureRandom();
    
    //Easier to have the processFiles in here as well than to try and call them from RSAFunctions.java
    static private void processFile(Cipher ci,InputStream in,OutputStream out)
	throws javax.crypto.IllegalBlockSizeException,
	       javax.crypto.BadPaddingException,
	       java.io.IOException {
        
	byte[] ibuf = new byte[1024];
	int len;
	while ((len = in.read(ibuf)) != -1) {
	    byte[] obuf = ci.update(ibuf, 0, len);
	    if ( obuf != null ) out.write(obuf);
	}
	byte[] obuf = ci.doFinal();
	if ( obuf != null ) out.write(obuf);
    }
    
    static private void processFile(Cipher ci,String inFile,String outFile)
	throws javax.crypto.IllegalBlockSizeException,
	       javax.crypto.BadPaddingException,
	       java.io.IOException {
        
	try (FileInputStream in = new FileInputStream(inFile);
	     FileOutputStream out = new FileOutputStream(outFile)) {
		processFile(ci, in, out);
	    }
    }
    
    public static void aesEncrypt(PublicKey pub, PrivateKey pri, String inputFile,
            String mode) throws Exception{
        
        //Genereate secret
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
	kgen.init(128);
	SecretKey skey = kgen.generateKey();
        
        //IV for AES CBC
        byte[] iv = new byte[128/8];
	srandom.nextBytes(iv);
	IvParameterSpec ivspec = new IvParameterSpec(iv);
        
        //Output the inputFile with .enc encrypting the AES secret with RSA
        try (FileOutputStream out = new FileOutputStream(inputFile + ".enc")) {
		//Either sign or encrypt using RSA to secure the AES secret
                //Sign if you want everyone to know its your file or encrypt if you want to send to one person only
                {
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    //If mode is set to sign encrypt with private key so anyone can decrypt using public key
                     if(mode == "sign"){
                         cipher.init(Cipher.ENCRYPT_MODE, pri);
                     } else {
                         cipher.init(Cipher.ENCRYPT_MODE, pub);
                     }
                     
                    byte[] b = cipher.doFinal(skey.getEncoded());
		    out.write(b);
		    System.err.println("AES Key Length: " + b.length);
		}
                
                //Output the iv used
		out.write(iv);
		System.err.println("IV Length: " + iv.length);
                
                //Encrypt the input file with AES in CBC mode
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		try (FileInputStream in = new FileInputStream(inputFile)) {
			processFile(ci, in, out);
		    }
	    }
    }
    
    public static void aesDecrypt(PublicKey pub, PrivateKey pri, String inputFile,
            String mode) throws Exception{
        
        int index = 0;

	try (FileInputStream in = new FileInputStream(inputFile)) {
		SecretKeySpec skey = null;
		//Get the skey to decrypt AES portion
                {
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    //If mode set to verify it will decrypt inputFile using public key
                    if(mode == "verify"){
                        cipher.init(Cipher.DECRYPT_MODE, pub);
                    } else {
                        cipher.init(Cipher.DECRYPT_MODE, pri);
                    }
                    
		    byte[] b = new byte[256];
		    in.read(b);
		    byte[] keyb = cipher.doFinal(b);
		    skey = new SecretKeySpec(keyb, "AES");
		}
                
                //Set the initialization vector from inputFile
		byte[] iv = new byte[128/8];
		in.read(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);
                
                //Create AES CBC Cipher to decrypt AES portion
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.DECRYPT_MODE, skey, ivspec);
                
                //Try to decrypt inputFIle and add extension .ver
		try (FileOutputStream out = new FileOutputStream(inputFile+".ver")){
			processFile(ci, in, out);
		    }
	    }
    }
}
