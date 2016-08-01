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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manage settings.
 * @author Matthias Fischer
 */
public class Settings {
    
    private final String path = "./settings.ini";
    private final Map<String, Setting> list;
    
    Settings() {
        EditFiles.createFile(this.path);
        this.list = new HashMap<>();
    }
    
    /**
     * Load settings out of the settings.ini file
     */
    public void loadSettings() {
        this.parseSettingFile(EditFiles.readFile(this.path));
    }
    
    /**
     * Save settings into the settings.ini file
     */
    public void saveSettings() {
        ObservableList<String> rows = FXCollections.observableArrayList();

        list.entrySet().stream().forEach((entry) -> {
            rows.add(entry.getValue().createFileString());
        });
        
        EditFiles.saveFile(this.path, rows);
    }
    
    /**
     * Setter for boolean setting - overides if this setting already exists
     * @param key Key of the setting
     * @param value Value of this setting
     */
    public void setSettingBoolean(String key, Boolean value) {
        Setting tmpSetting = new Setting(key, String.valueOf(value));
        this.list.put(key, tmpSetting);
    }
    
    /**
     * Getter for boolean setting
     * @param key Key of the setting
     * @return Boolean value of the setting - null if not available
     */
    public Boolean getSettingBoolean(String key) {
        Setting tmp = this.list.get(key);
        if(tmp != null) {
            return tmp.getValue().equals("true");
        }
        return false;
    }
    
    /**
     * Parses settings.ini file
     * @param list Rows of settings.ini file
     */
    private void parseSettingFile(List<String> list) {
        Setting tmpSetting;
        for(int i = 0; i < list.size(); i++) {
            tmpSetting = Setting.instanceOf(list.get(i)); 
            this.list.put(tmpSetting.getKey(), tmpSetting);
        }
    }
}
