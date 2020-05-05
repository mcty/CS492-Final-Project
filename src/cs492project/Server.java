package cs492project;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * File:
 * Server.java 
 * 
 * Purpose:
 * This file is used to start up a server socket that will take requests from
 * clients
 * 
 * References for documentation:
 * - 
 * 
 * @author tts-macbook
 */

public class Server {
  public static void main(String[] args) throws IOException {
        
        //Method to load Server Public and Private Key
        System.out.println("Server: Loading Server RSA KeyPair...");
        
        
        //Start up Server socket
        ServerSocket servsock = new ServerSocket(4333);
        System.out.println("Server: Server started...");
      
        //Transfer key to Client
        System.out.println("Server: Sending Client Server Public Key...");
        File myFile = new File("Public.pub");
        Socket sock = servsock.accept();
        while (true) {
            byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            //sock.close();
            break;
        }
        System.out.println("Server: Sent Server Public Key...");
        
        
        //Recieve Key from client
        System.out.println("Server: Recieving Client Public Key...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("new2_" + "Public.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Server: Recieved Client Public Key...");
        
        
        //Method to Load Client Public Key
        
        
        //Method to create authentication to send to client
        
        
        //Send file to Client that Server encrypted with their private key
        System.out.println("Server: Sending Server Authentication to Client...");
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
        System.out.println("Server: Authentication Sent...");
        
        
        //Recive File from Client that Client encrypted with their Private key
        System.out.println("Server: Recieve Authentication from Client...");
        mybytearray = new byte[1024];
        is = sock.getInputStream();
        fos = new FileOutputStream("new4_" + "Public.pub");
        bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        System.out.println("File Transfered...");
        bos.flush();
        sock.close();
        System.out.println("Server: Client Authentication Recieved...");
        
        
        //Method to verify Client
        
    }
}
