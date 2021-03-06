/*
 * The MIT License
 *
 * Copyright 2016 mfi.
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
package net.softwarebude.timetracker;

import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.softwarebude.io.EditFiles;

/**
 * Functioncollection to edit Task-files.
 * @author Matthias
 */
public class EditTaskFiles extends EditFiles {
    
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
        
        EditTaskFiles.saveFile(path, list);
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
        return EditTaskFiles.getDirectory(date) + EditTaskFiles.getFilename(date) + ".txt";
    }
}
