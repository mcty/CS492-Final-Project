/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs492project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
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
 * - https://www.novixys.com/blog/encrypt-sign-file-using-rsa-java/#5_Encrypt_and_Save_the_AES_Encryption_Key
 * 
 * @author tts-macbook
 */

public class newAESFunctions {
    static SecureRandom srandom = new SecureRandom();
    
    //Easier to have the processFiles in here as well than to try and call them from RSAFunctions.java
    static private void processSignFile(Cipher ci, Signature sign, InputStream in,OutputStream out)
	throws javax.crypto.IllegalBlockSizeException,
	       javax.crypto.BadPaddingException,
	       java.io.IOException,
	       SignatureException {
        
	byte[] ibuf = new byte[1024];
	int len;
	while ((len = in.read(ibuf)) != -1) {
            sign.update(ibuf, 0, len);
	    byte[] obuf = ci.update(ibuf, 0, len);
	    if ( obuf != null ) out.write(obuf);
	}
	byte[] obuf = ci.doFinal();
	if ( obuf != null ) out.write(obuf);
    }
    
    static private void processAuthFile(Cipher ci,Signature ver,InputStream in,OutputStream out,long dataLen)
        throws javax.crypto.IllegalBlockSizeException,
               javax.crypto.BadPaddingException,
               java.security.SignatureException,
               java.io.IOException {

        byte[] ibuf = new byte[1024];
        while (dataLen > 0) {
            int max = (int)(dataLen > ibuf.length ? ibuf.length : dataLen);
            int len = in.read(ibuf, 0, max);
            if ( len < 0 ) throw new java.io.IOException("Insufficient data");
            dataLen -= len;
            byte[] obuf = ci.update(ibuf, 0, len);
            if ( obuf != null ) {
                out.write(obuf);
                ver.update(obuf);
            }
        }
        byte[] obuf = ci.doFinal();
        if ( obuf != null ) {
            out.write(obuf);
            ver.update(obuf);
        }
    }
    
    public static void aesEncrypt(PublicKey otherPub, PrivateKey pri, String inputFile,
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
		//Either sign and encrypt using RSA to secure the AES secret
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, otherPub);
                byte[] b = cipher.doFinal(skey.getEncoded());
		out.write(b);
                //Output the iv used
		out.write(iv);
                
                //Create Signature
                Signature sign = Signature.getInstance("SHA256withRSA");
                sign.initSign(pri); // Sign using A's private key
                
                //Encrypt the input file with AES in CBC mode
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);
		try (FileInputStream in = new FileInputStream(inputFile)) {
                    processSignFile(ci, sign, in, out);
		}
                byte[] s = sign.sign();
                out.write(s);
                out.close();
	    }
    }
    
    public static void aesDecrypt(PublicKey otherPub, PrivateKey pri, String inputFile,
            String mode) throws Exception{
        
        int index = 0;
        
        long dataLen = new File(inputFile).length()
            - 256       // AES Key
            - 16        // IV
            - 256;      // Signature
        
	try (FileInputStream in = new FileInputStream(inputFile)) {
		SecretKeySpec skey = null;
                
		//Get the skey to decrypt AES portion
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, pri);
                    
		byte[] b = new byte[256];
		in.read(b);
		byte[] keyb = cipher.doFinal(b);
		skey = new SecretKeySpec(keyb, "AES");
                
                //Set the initialization vector from inputFile
		byte[] iv = new byte[128/8];
		in.read(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);
                
                Signature ver = Signature.getInstance("SHA256withRSA");
                ver.initVerify(otherPub);
                
                //Create AES CBC Cipher to decrypt AES portion
		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.DECRYPT_MODE, skey, ivspec);
                
                //Try to decrypt inputFIle and add extension .ver
		try (FileOutputStream out = new FileOutputStream(inputFile+".ver")){
			processAuthFile(ci, ver, in, out, dataLen);
		}
                
                byte[] s = new byte[256];
                int len = in.read(s);
                if ( ! ver.verify(s) ) {
                    System.err.println("Signature not valid...");
                }
                else{
                    System.out.println("Signature verified...");
                }
	    }
    }
}
