package cs492project;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import static java.lang.System.exit;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    public static void main(String[] args) throws Exception {
        
        //Method to load Server Public and Private Key
        System.out.println("Server: Loading Server RSA KeyPair...");
        KeyPair kp = RSAFunctions.generateKeyPair("server");
        PublicKey pub = kp.getPublic();
        PrivateKey pri = kp.getPrivate();
        
        //Start up Server socket
        ServerSocket servsock = new ServerSocket(4333);
        System.out.println("Server: Server started...");
        System.out.println("Server: Waiting for connection...");
        Socket sock = servsock.accept();
        System.out.println("Client Connected...");
        
        //To read data coming from the client and string to store recieved data
        BufferedReader brClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        //To send data to client
        PrintStream ps = new PrintStream(sock.getOutputStream());
        String str, temp;
        int size;
        
        //While loop to switch between commands sent from the client
        while((str = brClient.readLine()) != null){
            System.out.println("Server: Recieved command " + str);
            if(Integer.parseInt(str) == 1){
                temp = brClient.readLine();
                System.err.println(temp);
                size = Integer.parseInt(brClient.readLine());
                System.err.println(size);
                recieveFile(sock, pri, temp, size);
            } else if(Integer.parseInt(str) == 2){
                //Does not check if server has the file or not...
                temp = brClient.readLine();
                size = getFileSize(temp);
                ps.println(size);
                Thread.sleep(100);
                sendFile(sock, pri, temp);
            } else if(Integer.parseInt(str) == 3){
                exchangeKeys(sock);
            } else if(Integer.parseInt(str) == 4){
                break;
            } else {
                System.out.println("Server: Invalid Input recieved");
            }
            
        }
        brClient.close();
        sock.close();
        exit(0);
    }
    
    public static int getFileSize(String fileName){
        File myFile = new File(fileName);
        int size = (int) myFile.length();
        return size;
    }
    
    public static void sendFile(Socket sock, PrivateKey pri, String fileName) throws FileNotFoundException, IOException, Exception{
        //Get server public key
        PublicKey clientPub = RSAFunctions.getPublicKey("server_saved_clientPublic.pub");
        
        //Create the encrypted and signed file
        newAESFunctions.aesEncrypt(clientPub, pri, fileName, "encrypt");
        System.out.println("Server: File AES encrypted...");
        
        //Send Client Public key to server
        System.out.println("Server: Sending " + fileName + " to client...");
        File myFile = new File(fileName + ".enc");
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
        System.out.println("Server: Sent file to client...");
    }
    
    public static void send(Socket sock) throws FileNotFoundException, IOException{
        System.out.println("Server: Sending Client Server Public Key...");
        File myFile = new File("serverPublic.pub");
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
    }
    
    public static void recieveFile(Socket sock, PrivateKey pri, String fileName, int size) throws IOException, Exception{
        //Recieve File from client
        System.out.println("Server: Recieving Client File...");
        byte[] mybytearray = new byte[(size + 1024)];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("client_file_" + fileName + ".enc");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Server: Recieved Client file...");
        
        //Decrypt File recieved
        System.out.println("Server: Decrypting Recieved file...");
        PublicKey clientPub = RSAFunctions.getPublicKey("server_saved_clientPublic.pub");
        
        String result = "client_file_" + fileName + ".enc";
        newAESFunctions.aesDecrypt(clientPub, pri, result, "decrypt");
        System.out.println("Server: File AES decrypted...");
    }
    
    public static void recieve(Socket sock) throws IOException{
        //Recieve Key from client
        System.out.println("Server: Recieving Client Public Key...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("new_" + "clientPublic.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Server: Recieved Client Public Key...");
    }
    
    
    //Exchange public keys between client and server
    public static void exchangeKeys(Socket sock) throws FileNotFoundException, IOException{
        //Recieve Key from client
        System.out.println("Server: Recieving Client Public Key...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("server_saved_" + "clientPublic.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Server: Recieved Client Public Key...");
        
        System.out.println("Server: Sending Client Server Public Key...");
        File myFile = new File("serverPublic.pub");
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
        System.out.println("Server: Sent Client Server Public Key...");
    }
}
