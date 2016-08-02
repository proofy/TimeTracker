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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A collection of functions to interact with files.
 * @author Matthias Fischer
 */
public final class EditFiles {
    
    /**
     * Creates a file in a specific location.
     * @param path Path to file
     */
    public static void createFile(String path) {
        createFile(path, false);
    }
    
    /**
     * Creates a file or a directory in a specific location.
     * @param path Path to file
     * @param isDirectory Is a directory? yes = true : no = false
     */
    public static void createFile(String path, Boolean isDirectory) {
        try {
            File file = new File(path);
            if(!file.exists()) {
                 if(isDirectory) {
                     file.mkdirs();
                 } else {
                     file.createNewFile();
                 }
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
        
    }
    
    /**
     * Reads all lines of a file.
     * @param path Path to file
     * @return List of strings
     */
    public static List<String> readFile(String path) {
        String line;
        List<String> records = new ArrayList<>();
        File file = new File(path);
        
        if(file.exists()) {
            try {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        records.add(line);
                    }
                    bufferedReader.close();
                }
            } catch(IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return records;
    }
    
    /**
     * Writes tasks into a specific file.
     * @param path Path to file
     * @param data Tasks that should be saved
     */
    public static void saveTasks(String path, ObservableList<Task> data) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i = 0; i < data.size(); i++) {
            list.add(data.get(i).toFileString());
        }
        
        saveFile(path, list);
    }
    
    /**
     * Writes strings into a specific file.
     * @param path Path to file
     * @param strings Strings that should be saved
     */
    public static void saveFile(String path, ObservableList<String> strings) {
        Writer writer = null;
        
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for(int i=0; i < strings.size(); i++) {
                writer.append(strings.get(i) + "\n");
            }
            writer.close();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {if(writer != null) writer.close();} catch (Exception e) {}
        }
    }
    
    /**
     * Converts a date into a filename
     * @param date Date that should be converted
     * @return filename as a String
     */
    public static String getFilename(Calendar date) {
        String year = Integer.toString(date.get(Calendar.YEAR));
        String month = Task.correctLength(Integer.toString(date.get(Calendar.MONTH)+1));
        String day = Task.correctLength(Integer.toString(date.get(Calendar.DAY_OF_MONTH)));
        return year + "-" + month + "-" + day;
    }
    
    /**
     * Convert a date into a directory-hierarchie.
     * @param date Date that should be converted
     * @return Directory-hierarchie as a String
     */
    public static String getDirectory(Calendar date) {
        String year = Integer.toString(date.get(Calendar.YEAR));
        String month = Task.correctLength(Integer.toString(date.get(Calendar.MONTH)+1));
        
        return "./files/" + year + "/" + month + "/";
    }
    
    /**
     * Converts a date into a filepath with .txt-Extension.
     * @param date Date that should be converted
     * @return filepath as a String
     */
    public static String getFilePath(Calendar date) {
        return EditFiles.getDirectory(date) + EditFiles.getFilename(date) + ".txt";
    }
}
