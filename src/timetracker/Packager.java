/*
 * The MIT License
 *
 * Copyright 2016 Matthias Fischer.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Collection of packaging functions.
 * @author Matthias Fischer
 */
public class Packager {
    
    /**
     * This method zips the contant of a directory into a file.
     * @param targetPath Content that should be zipped.
     * @param zipFile Target file
     */
    public static void zip(String targetPath, File zipFile) {
        FileOutputStream fos;
        ZipOutputStream zos;
        String filePath;
        
        try {
            byte[] buffer = new byte[1024];
            File targetFile = new File(targetPath);
            List<String> targetFiles = Packager.generateFileList(targetFile);
            
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
                
            for(int i = targetFiles.size()-1; i >= 0; i--) {
                try {
                    int len;
                    FileInputStream in;
                    filePath = targetFiles.get(i);
                    filePath = filePath.subSequence(targetFile.getPath().length()+1, filePath.length()).toString();

                    ZipEntry ze = new ZipEntry(filePath);
                    zos.putNextEntry(ze);

                    in = new FileInputStream(new File(targetFile.getAbsolutePath() + File.separator + filePath));

                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    in.close();

                } catch (FileNotFoundException e) {
                    System.err.println(e.getMessage());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }

            zos.closeEntry();
            zos.close();
            fos.close();
            
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * This method unzips a file into a directory.
     * @param zipFile File that should be unziped.
     * @param targetDirectory Targetdirectory
     */
    public static void unzip(File zipFile, String targetDirectory) {
        byte[] buffer = new byte[1024];
        
        try {
            
            File folder = new File(targetDirectory);
            if(!folder.exists()) {
                folder.mkdir();
            }
            
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            
            while(zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(targetDirectory + File.separator + fileName);
                
                new File(newFile.getParent()).mkdirs();
                
                if(!fileName.contains(".")) {
                    newFile.mkdir();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
 
                zipEntry = zipInputStream.getNextEntry();
            }
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Lists all files in a specific directory and returns them as a arraylist of strings.
     * @param node Targetdirectory
     * @return ArrayList of files
     */
    public static List<String> generateFileList(File node) {
        List<String> list = new ArrayList<>();
        if(node.isFile()) {
            list.add(node.getPath());
        }
        
        if(node.isDirectory()) {
            String[] subNodes = node.list();
            List<String> tmpArraylist;
            
            for(String filename : subNodes) {
                tmpArraylist = Packager.generateFileList(new File(node.getPath()+ File.separator + filename));
                tmpArraylist.forEach(string -> {
                    list.add(string);
                });
            }
        }
        
        return list;
    }
}
