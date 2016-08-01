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

import java.util.Calendar;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represent one task.
 * @author Matthias Fischer
 */
public class Task {
    
    private Boolean started = false;
        
    private Calendar dateStart;
    private Calendar dateEnd;
    
    private SimpleStringProperty start;
    private SimpleStringProperty end;
    private SimpleStringProperty project;
    private SimpleStringProperty description;
    
    /**
     * Constructor for new tasks
     * @param project Project name
     * @param description Project description
     */
    Task(String project, String description) {
        this.project = new SimpleStringProperty(project);
        this.description = new SimpleStringProperty(description);
    }
    
    /**
     * Constructor for tasks out of a file
     * @param fileString Saved task row of file
     */
    Task(String fileString) {
        this.parseFileString(fileString);
    }
    
    /**
     * Start recording time
     */
    public void start() {
        this.dateStart = Calendar.getInstance(Locale.GERMANY);
        this.start = new SimpleStringProperty(this.timeToString(dateStart));
        this.started = true;
    }
    
    /**
     * Stop recording time
     */
    public void stop() {
        this.dateEnd = Calendar.getInstance(Locale.GERMANY);
        this.end = new SimpleStringProperty(this.timeToString(dateEnd));
        this.started = false;
    }
    
    /**
     * Getter of the start time
     * @return Start time as String
     */
    public String getStart() {
        return this.start.getValue();
    }
    
    /**
     * Getter of the end time
     * @return End time as String
     */
    public String getEnd() {
        return this.end.getValue();
    }
    
    /**
     * Getter of the project name
     * @return Projectname as String
     */
    public String getProject() {
        return this.project.getValue();
    }
    
    /**
     * Setter of the project name
     * @param s Projectname as String
     */
    public void setProject(String s) {
        this.project.setValue(s);
    }
    
    /**
     * Getter of the project description
     * @return Project description as String
     */
    public String getDescription() {
        return this.description.getValue();
    }
    
    /**
     * Setter of the project description
     * @param s Project description as String
     */
    public void setDescription(String s) {
        this.description.setValue(s);
    }
    
    /**
     * Set start time of a task in "hh:mm" format
     * @param s time
     * @return Boolean checks if the values are okay.
     */
    public Boolean setStartTime(String s) {
        
        String str[] = s.split(":");
        int hours = Integer.parseInt(str[0]);
        int minutes = Integer.parseInt(str[1]);
        
        if(hours >= 0 && hours < 25 && minutes >= 0 && minutes < 60) {
            this.dateStart.set(Calendar.HOUR_OF_DAY, hours);
            this.dateStart.set(Calendar.MINUTE, minutes);
            return true;
        }
        return false;
    }
    
    /**
     * Get start time of a task in "hh:mm" format
     * @return String "hh:mm" of start time
     */
    public String getStartTime() {
        return (this.dateStart != null) ? this.timeToString(dateStart) : "";
    }
    
    /**
     * Set end time of a task in "hh:mm" format
     * @param s time
     * @return Boolean checks if the values were okay
     */
    public Boolean setEndTime(String s) {
        
        String str[] = s.split(":");
        int hours = Integer.parseInt(str[0]);
        int minutes = Integer.parseInt(str[1]);
        
        if(hours >= 0 && hours < 25 && minutes >= 0 && minutes < 60) {
            this.dateEnd.set(Calendar.HOUR_OF_DAY, hours);
            this.dateEnd.set(Calendar.MINUTE, minutes);
            return true;
        }
        return false;
    }
    
    /**
     * Get end time of a task in "hh:mm" format
     * @return String "hh:mm" of end time
     */
    public String getEndTime() {
        return (this.dateEnd != null) ? this.timeToString(dateEnd) : "";
    }
    
    /**
     * Time recording activated?
     * @return Boolean
     */
    public Boolean isStarted() {
        return this.started;
    }
    
    /**
     * Convert task into string
     * @return Task as String
     */
    public String toFileString() {
        return this.project.getValue() + ";;;" 
                + this.description.getValue() + ";;;" 
                + Long.toString(this.dateStart.getTimeInMillis()) + ";;;" 
                + Long.toString(this.dateEnd.getTimeInMillis()) + ";;;";
    }
    
    /**
     * Corrects length of a day/month-string.
     * Examples: 
     * 1. input is  '2' = output is '02'
     * 2. input is '02' = output is '02'
     * @param str Input String
     * @return Corrected String
     */
    public static String correctLength(String str) {
        return (str.length() == 1) ? "0" + str : str;
    }
    
    /**
     * Parse filestring into a task values
     * @param str Filestring
     */
    private void parseFileString(String str) {
        String[] string = str.split(";;;");
        this.project = new SimpleStringProperty(string[0]);
        this.description = new SimpleStringProperty(string[1]);
        
        this.dateStart = Calendar.getInstance(Locale.GERMANY);
        this.dateStart.setTimeInMillis(Long.parseLong(string[2], 10));
        this.start = new SimpleStringProperty(this.timeToString(this.dateStart));
        
        this.dateEnd = Calendar.getInstance(Locale.GERMANY);
        this.dateEnd.setTimeInMillis(Long.parseLong(string[3], 10));
        this.end = new SimpleStringProperty(this.timeToString(this.dateEnd));
    }
    
    /**
     * Parse date into a time string like '12:42'
     * @param cal Calender that should be converted
     * @return Time as String
     */
    private String timeToString(Calendar cal) {
        return Task.correctLength(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) 
                + ":" 
                + Task.correctLength(Integer.toString(cal.get(Calendar.MINUTE)));
    }
}
