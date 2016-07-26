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

/**
 * Simple setting of the file settings.ini
 * @author Matthias Fischer
 */
public class Setting {
    
    private String key;
    private String value;
    
    /**
     * Constructor of a setting
     * @param key Key of the setting
     * @param value Value of the setting
     */
    Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Creates an instance of the class Setting.
     * @param line Row of the file settings.ini
     * @return Setting instance
     */
    public static Setting instanceOf(String line) {
        String[] str = line.split("=");
        return new Setting(str[0], str[1]);
    }
    
    /**
     * Getter of keyname
     * @return String key
     */
    public String getKey() {
        return this.key;
    }
    
    /**
     * Getter of value
     * @return String value
     */
    public String getValue() {
        return this.value;
    }
}
