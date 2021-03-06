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
package net.softwarebude.timetracker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

/**
 * FXML Controller class for the Settings dialog
 *
 * @author Matthias Fischer
 */
public class SettingsController implements Initializable {
    
    private final static String USESYSTEMTRAY = "useSystemTray";
    
    private Settings settings;
    @FXML private CheckBox cbUseSystemTray;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.settings = new Settings();
        this.settings.loadSettings();
        
        if(this.settings.getSettingBoolean(SettingsController.USESYSTEMTRAY)) {
            cbUseSystemTray.fire();
        }
    }
    
    @FXML
    public void handleUseSystemTrayCheckboxAction(ActionEvent e) {
        if(this.settings.getSettingBoolean(SettingsController.USESYSTEMTRAY)) {
            this.settings.setSettingBoolean(SettingsController.USESYSTEMTRAY, false);
        } else {
            this.settings.setSettingBoolean(SettingsController.USESYSTEMTRAY, true);
        }
        
        this.settings.saveSettings();
    }
}
