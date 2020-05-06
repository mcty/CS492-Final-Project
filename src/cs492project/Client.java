package cs492project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.exit;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

/**
 *
 * @author tts-macbook
 */

public class Client {
    public static void main(String[] argv) throws Exception {
        
        //Method to load in Client Public and Private Key
        System.out.println("Client: Loading Client RSA KeyPair...");
        
        //Load or generate RSA keypair for client
        KeyPair kp = RSAFunctions.generateKeyPair("client");
        PublicKey pub = kp.getPublic();
        PrivateKey pri = kp.getPrivate();
        
        //Connect to server
        Socket sock = new Socket("localhost", 4333);
        System.out.println("Client: Establishing Connection to server...");
        
        //Send data to server and input to get command from client
        DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
        //To read data coming from the server
        BufferedReader brServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        Scanner input = new Scanner(System.in);
        int size;
        
        //Take commands from client
        while(true){
            System.out.println("1: Send File\n" + "2: Recieve File\n" + "3: Share Key\n" + "4: Exit");
            int myInt = input.nextInt();
            System.out.println("Client: Command entered by user " + myInt);
            if(myInt == 1){
                dos.writeBytes(myInt + "\n");
                dos.writeBytes("SecretFile.docx" + "\n");
                size = getFileSize("SecretFile.docx");
                dos.writeBytes(size + "\n");
                Thread.sleep(100);
                sendFile(sock, pri, "SecretFile.docx");
            } else if(myInt == 2){
                dos.writeBytes(myInt + "\n");
                dos.writeBytes("SecretServerFile.docx" + "\n");
                size = Integer.parseInt(brServer.readLine());
                System.err.println(size);
                recieveFile(sock, pri, "SecretServerFile.docx", size);
            } else if(myInt == 3){
                dos.writeBytes(myInt + "\n");
                Thread.sleep(100);
                exchangeKeys(sock);
            } else if(myInt == 4){
                break;
            } else {
                System.out.println("Client: Invalid Input");
            }
        }
        dos.close();
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
        PublicKey serverPub = RSAFunctions.getPublicKey("client_saved_serverPublic.pub");
        
        //Create the encrypted and signed file
        newAESFunctions.aesEncrypt(serverPub, pri, fileName, "encrypt");
        System.out.println("Client: File AES encrypted...");
        
        //Send Client Public key to server
        System.out.println("Client: Sending " + fileName + " to server...");
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
        System.out.println("Client: Sent file to server...");
    }
    
    public static void send(Socket sock) throws FileNotFoundException, IOException{
        //Send Client Public key to server
        System.out.println("Client: Sending Client Public Key...");
        File myFile = new File("clientPublic.pub");
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
        System.out.println("Client: Sent Client Public Key...");
    }
    
    public static void recieveFile(Socket sock, PrivateKey pri, String fileName, int size) throws IOException, Exception{
        //Recieve File from server
        System.out.println("Client: Recieving Client File...");
        byte[] mybytearray = new byte[(size + 1024)];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("server_file_" + fileName + ".enc");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Client: Recieved Client file...");
        
        //Decrypt File recieved
        System.out.println("Client: Decrypting Recieved file...");
        PublicKey serverPub = RSAFunctions.getPublicKey("client_saved_serverPublic.pub");
        
        String result = "server_file_" + fileName + ".enc";
        newAESFunctions.aesDecrypt(serverPub, pri, result, "decrypt");
        System.out.println("Client: File AES decrypted...");
    }
    
    public static void recieve(Socket sock) throws IOException{
        //Recive File from Server that Server encrypted with their Private key
        System.out.println("Client: Recieving Authentication from Server...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("new_" + "serverPublic.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Client: Server Public Key Recieved...");
    }
    
    public static void exchangeKeys(Socket sock) throws FileNotFoundException, IOException{
        //Send Client Public key to server
        System.out.println("Client: Sending Client Public Key to server...");
        File myFile = new File("clientPublic.pub");
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
        System.out.println("Client: Sent Client Public Key to server...");
        
        //Recive File from Server that Server encrypted with their Private key
        System.out.println("Client: Recieving Server Public Key...");
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("client_saved_" + "serverPublic.pub");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.flush();
        //sock.close();
        System.out.println("Client: Recieved Server Public Key...");
    }
}