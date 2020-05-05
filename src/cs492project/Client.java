package cs492project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author tts-macbook
 */

public class Client {
    public static void main(String[] argv) throws Exception {
        
        //Method to load in Client Public and Private Key
        System.out.println("Client: Loading Client RSA KeyPair...");
        
        
        //Select File to send to server
        
        
        
        //Connect to server
        Socket sock = new Socket("localhost", 4333);
        System.out.println("Client: Establishing Connection to server...");

        
        //Recieve Server Public key
        System.out.println("Client: Obtaining Server Public Key...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("new1_" + "Public.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Client: Server Public Key Recieved...");
        
        
        //Method to Load Server Public key
        

        //Send Client Public key to server
        System.out.println("Client: Sending Client Public Key...");
        File myFile = new File("Public.pub");
        while (true) {
            mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            //sock.close();
            break;
        }
        System.out.println("Client: Sent Client Public Key...");

        //Recive File from Server that Server encrypted with their Private key
        System.out.println("Client: Recieving Authentication from Server...");
        mybytearray = new byte[1024];
        is = sock.getInputStream();
        fos = new FileOutputStream("new3_" + "Public.pub");
        bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Client: Authentication Recieved...");
        
        
        //Method to verify File that server sent
        
        
        //Method to create file to send to Server
        
        
        //Send file to Server that Client encrypted with their private key
        System.out.println("Client: Sending Authentication to Server...");
        myFile = new File("Public.pub");
        while (true) {
            mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            //sock.close();
            break;
        }
        System.out.println("Client: Authentication Sent...");
        
        
    }  
}