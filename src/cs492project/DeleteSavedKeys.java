package cs492project;

import java.io.File;

/**
 * File:
 * DeleteSavedKeys.java 
 * 
 * Purpose:
 * This file  is used to delete saved keys from the run location of the program.
 * 
 * References for documentation:
 * - https://examples.javacodegeeks.com/core-java/io/file/delete-files-with-certain-extension-only-using-filenamefilter-in-java/
 * 
 * @author tts-macbook
 */

public class DeleteSavedKeys {
 
    public static void deleteSavedKeys() {
        
        String parentDirectory = System.getProperty("user.dir");;
        String[] deleteExtension= {".key",".pub"};
        
        for(int i=0; i <=1; i++){
            FileFilter fileFilter = new FileFilter(deleteExtension[i]);
            File parentDir = new File(parentDirectory);
 
            // Put the names of all files ending with .txt in a String array
            String[] listOfTextFiles = parentDir.list(fileFilter);
 
            if (listOfTextFiles.length == 0) {
                System.out.println("There are no keys stored in this direcotry...");
                return;
            }
 
            File fileToDelete;
 
            for (String file : listOfTextFiles) {
 
                //construct the absolute file paths...
                String absoluteFilePath = new StringBuffer(parentDirectory).append(File.separator).append(file).toString();
 
                //open the files using the absolute file path, and then delete them...
                fileToDelete = new File(absoluteFilePath);
                boolean isdeleted = fileToDelete.delete();
                System.out.println("File : " + absoluteFilePath + " was deleted : " + isdeleted);
            }
        }
    }
}
