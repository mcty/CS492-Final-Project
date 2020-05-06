# CS492-Final-Project

This is the final project for CS492 security and is a secure encrypted file transfer between client and server that provides verification.
The file is encrypted using AES in CBC mode. The AES secretKey is encrypted using RSA in EBC mode and verification is provided by sending a signed bit of data which is encrypted using the senders private RSA key.
With this we can show that verification is ensured and only the reciever can decrypt the AES secretKey with their private RSA key and then decrypt the file with the AES secretKey.

Notes:
-The Ssh.java file is a test file used in CS492FinalProject.java

-The SecretFile.docx and SecretServerFile.docx are files used to show functionality in the client.java and server.java to transfer files.

-To use run the files server.java and then client.java
