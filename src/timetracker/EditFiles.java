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
 *
 * @author mfi
 */
public final class EditFiles {
    
    
    public static void createFile(String path) {
        createFile(path, false);
    }
    
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
            e.printStackTrace();
        }
        
    }
    
    public static List<String> readFile(String path) {
        String line = null;
        List<String> records = new ArrayList<>();
        File file = new File(path);
        
        if(file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

                while ((line = bufferedReader.readLine()) != null)
                {
                    records.add(line);
                }

                bufferedReader.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return records;
    }
    
    public static void saveIssues(String path, ObservableList<Task> data) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for(int i = 0; i < data.size(); i++) {
            list.add(data.get(i).toFileString());
        }
        
        saveFile(path, list);
    }
    
    public static void saveFile(String path, ObservableList<String> data) {
        Writer writer = null;
        
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for(int i=0; i < data.size(); i++) {
                writer.append(data.get(i) + "\n");
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {if(writer != null) writer.close();} catch (Exception e) {}
        }
    }
    
    public static String getDirectory(Calendar date) {
        String year = Integer.toString(date.get(Calendar.YEAR));
        String month = Task.correctLength(Integer.toString(date.get(Calendar.MONTH)));
        
        return "./files/" + year + "/" + month + "/";
    }
    
    public static String getFilePath(Calendar date) {
        
        String year = Integer.toString(date.get(Calendar.YEAR));
        String month = Task.correctLength(Integer.toString(date.get(Calendar.MONTH)));
        String day = Task.correctLength(Integer.toString(date.get(Calendar.DAY_OF_MONTH)));
        
        return getDirectory(date) + year + "-" + month + "-" + day + ".txt";
    }
}
