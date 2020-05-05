package cs492project;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File:
 * FileFilter.java 
 * 
 * Purpose:
 * This file  is used for DeleteSavedKeys.java to filter files based on the input
 * extension.
 * 
 * References for documentation:
 * - https://examples.javacodegeeks.com/core-java/io/file/delete-files-with-certain-extension-only-using-filenamefilter-in-java/
 * 
 * @author tts-macbook
 */
 
public class FileFilter implements FilenameFilter {
 
    private String fileExtension;
 
    public FileFilter(String fileExtension) {
        this.fileExtension = fileExtension;
    }
 
    @Override
    public boolean accept(File directory, String fileName) {
        return (fileName.endsWith(this.fileExtension));
    }
}
