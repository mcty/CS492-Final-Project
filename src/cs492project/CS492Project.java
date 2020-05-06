/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs492project;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author tts-macbook
 */
public class CS492Project {
    
    public static void main(String args[]) throws Exception{
        
        System.out.println("Starting program...");
        System.out.println("Starting tests...");
        
        KeyPair kp = RSAFunctions.generateKeyPair("client");
        PublicKey pub = kp.getPublic();
        PrivateKey pri = kp.getPrivate();
        
        KeyPair kps = RSAFunctions.generateKeyPair("server");
        PublicKey serverPub = kps.getPublic();
        PrivateKey serverPri = kps.getPrivate();
        
        String dir = System.getProperty("user.dir");
        String inputFile = dir + "/SecretFile.docx";
        
    
    //Test new AES + CBC    
        if(false){
            //Test File Encryption with public key For Client to send file
            newAESFunctions.aesEncrypt(serverPub, pri, inputFile, "encrypt");
            System.out.println("File AES encrypted");
        }
        if(true){
            String result = inputFile + ".enc";
            newAESFunctions.aesDecrypt(pub, serverPri, result, "decrypt");
            System.out.println("File AES decrypted");
        }
        
    //Test AES + CBC    
        if(false){
            //Test File Encryption with public key
            AESFunctions.aesEncrypt(pub, pri, inputFile, "encrypt");
            System.out.println("File AES encrypted");
        }
        if(false){
            String result = inputFile + ".enc";
            AESFunctions.aesDecrypt(pub, pri, result, "decrypt");
            System.out.println("File AES decrypted");
        }
        
    //Test RSA
        if(false){
            //Test File Encryption with public key
            RSAFunctions.rsaEncrypt(pub, pri, inputFile, "encrypt");
            System.out.println("File encrypted");
        }
        if(false){
            String result = inputFile + ".enc";
            RSAFunctions.rsaDecrypt(pub, pri, result, "decrypt");
            System.out.println("File decrypted");
        }
    //Delete Saved Keys    
        if(false){
            //Delete Saved Keys
            System.out.println("Deleting all saved keys...");
            DeleteSavedKeys.deleteSavedKeys();
        }
    //Use test method in RSAFunctions  
        if(false){
            //RSA Test
            System.out.println("RSA Test...");
            RSAFunctions.test();
        }
    }
    
}