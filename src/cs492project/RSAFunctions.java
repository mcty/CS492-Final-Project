package cs492project;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Arrays;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.io.File;
import java.nio.file.Path;

/**
 * File:
 * RSAFunctions.java 
 * 
 * Purpose:
 * This file  is used for RSA keypair generation, RSA encryption, and RSA 
 * decryption.
 * 
 * References for documentation:
 * - This source helped learn RSA
 * - https://www.novixys.com/blog/rsa-file-encryption-decryption-java/
 * - https://www.novixys.com/blog/using-aes-rsa-file-encryption-decryption-java/
 * - https://docs.oracle.com/javase/7/docs/api/java/security/KeyPair.html
 * 
 * - This source helped learn to check if files exist
 * - https://alvinalexander.com/java/java-file-exists-directory-exists/
 * - https://stackoverflow.com/questions/17939556/how-to-get-the-execution-directory-path-in-java
 * 
 * @author tts-macbook
 */

public class RSAFunctions {
    
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
    
    //Generates 2048 bit RSA keypair with SecureRandom unless one exists
    public static KeyPair generateKeyPair(String user) throws Exception {
        
        KeyPair pair;
        String dir = System.getProperty("user.dir");
        File file = new File(dir + "/" + user + "Private.key");
        
        if(file.exists() && file.isFile()){
            pair = recoverKeyPair(user);
            return pair;
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048, new SecureRandom());
            pair = kpg.generateKeyPair();
        
            saveKeyPair(pair, user);
            return pair;
        }
    }
    
    //Saves the keypair generated on first use
    public static void saveKeyPair(KeyPair kp, String user) throws Exception {
        try (FileOutputStream out = new FileOutputStream(user + "Private" + ".key")) {
            out.write(kp.getPrivate().getEncoded());
        }

        try (FileOutputStream out = new FileOutputStream(user + "Public" + ".pub")) {
            out.write(kp.getPublic().getEncoded());
        }
        
    }
    
    //Returns a keypair from files in run folder
    public static KeyPair recoverKeyPair(String user) throws Exception {
        
        String dir = System.getProperty("user.dir");
        
        Path pathPublic = Paths.get(dir + "/" + user + "Public.pub");
        byte[] bytesPublic = Files.readAllBytes(pathPublic);
        X509EncodedKeySpec ksPub = new X509EncodedKeySpec(bytesPublic);
        KeyFactory kfPub = KeyFactory.getInstance("RSA");
        PublicKey pub = kfPub.generatePublic(ksPub);
        
        
        Path pathPrivate = Paths.get(dir + "/" + user + "Private.key");
        byte[] bytesPrivate = Files.readAllBytes(pathPrivate);
        PKCS8EncodedKeySpec ksPri = new PKCS8EncodedKeySpec(bytesPrivate);
        KeyFactory kfPri = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kfPri.generatePrivate(ksPri);
        
        
        KeyPair pair = new KeyPair(pub, pvt);
        
        return pair;
    }
    
    public static PublicKey getPublicKey(String fileName) throws Exception {
        String dir = System.getProperty("user.dir");
        
        Path pathPublic = Paths.get(dir + "/" + fileName);
        byte[] bytesPublic = Files.readAllBytes(pathPublic);
        X509EncodedKeySpec ksPub = new X509EncodedKeySpec(bytesPublic);
        KeyFactory kfPub = KeyFactory.getInstance("RSA");
        PublicKey pub = kfPub.generatePublic(ksPub);
        
        return pub;
    }
    
    //Encrypts file with either public or private key depending on selected mode
    public static void rsaEncrypt(PublicKey pub, PrivateKey pri, String inputFile,
            String mode) throws Exception {
        //Anyone can decrypt this with your public key
        if(mode == "sign"){
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pri);
            processFile(cipher, inputFile, inputFile + ".enc");
        }
        //No one can decrypt this without the private key for the public key used
        else{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            processFile(cipher, inputFile, inputFile + ".enc");
        }
    }
    
    public static void rsaDecrypt(PublicKey pub, PrivateKey pri, String inputFile,
            String mode) throws Exception {
        if(mode == "verify"){
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pub);
            processFile(cipher, inputFile, inputFile + ".ver");
        }
        //
        else{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pri);
            processFile(cipher, inputFile, inputFile + ".ver");
        }
    }
    
    //Test class with tests created for demonstraiting functionality
    public static void test() throws Exception{
        KeyPair kp = generateKeyPair("test");
        PublicKey pub = kp.getPublic();
        PrivateKey pri = kp.getPrivate();
        
        //Tests
        //Show keypair
        System.out.println("Public RSA key:\n" + pub);
        System.out.println("Private RSA key:\n" + pri);
        
        //Testing getting file path
        String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
        File file = new File(dir + "/Private.key");
        if(file.exists() && file.isFile()){
            System.out.println("Private file found");
        }
    }
    
}
