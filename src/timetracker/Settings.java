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

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manage settings.
 * @author Matthias Fischer
 */
public class Settings {
    
    private final String path = "./settings.ini";
    
    private boolean autostart;
    
    Settings() {
        EditFiles.createFile(this.path);
    }
    
    /**
     * Load settings out of the settings.ini file
     */
    public void loadSettings() {
        this.parseSettings(EditFiles.readFile(this.path));
    }
    
    /**
     * Save settings into the settings.ini file
     */
    public void saveSettings() {
        ObservableList<String> rows = FXCollections.observableArrayList();
        
        rows.add(Setting.createFileString("autostart", String.valueOf(this.autostart)));
        
        EditFiles.saveFile(this.path, rows);
    }
    
    /**
     * Setter autostart application
     * @param b Boolean
     */
    public void setAutostart(Boolean b) {
        this.autostart = b;
    }
    
    /**
     * Getter autostart application
     * @return Boolean
     */
    public Boolean getAutostart() {
        return this.autostart;
    }
    
    /**
     * Parses settings.ini file
     * @param list Rows of settings.ini file
     */
    private void parseSettings(List<String> list) {
        Setting tmpSetting;
        for(int i = 0; i < list.size(); i++) {
            tmpSetting = Setting.instanceOf(list.get(i)); 
            switch(tmpSetting.getKey()) {
                case "autostart":
                    this.autostart = tmpSetting.getValue().equals("true");
                    break;
            }
        }
    }
}
