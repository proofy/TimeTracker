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

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.ImageIcon;

/**
 *
 * @author Matthias Fischer
 */
public class TimeTracker extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        
        Parent root = FXMLLoader.load(getClass().getResource("Application.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("TimeTracker");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.getIcons().add(new javafx.scene.image.Image("/images/logo.png"));
        
        stage.show();
        
        if (SystemTray.isSupported()) {         
            SystemTray tray = SystemTray.getSystemTray();

            PopupMenu popup = new PopupMenu();
            MenuItem itemOpen = new MenuItem("Open");
            itemOpen.addActionListener(new ActionListener() {                
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                            stage.toFront();
                        }
                    });
                }               
            });
            
            MenuItem itemExit = new MenuItem("Exit");
            
            popup.add(itemOpen);
            popup.add(itemExit);
            
            Image img = createImage("/images/logo.png", "TimeTracker");
            
            TrayIcon trayIcon = new TrayIcon(img, "TimeTracker", popup); 
            trayIcon.setImageAutoSize(true);
            
            trayIcon.addActionListener(new ActionListener() {                
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0) {                
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                   });
                }                   
            });

            itemExit.addActionListener(new ActionListener() {                
                @Override
                public void actionPerformed(java.awt.event.ActionEvent arg0) {
                    tray.remove(trayIcon);
                    System.exit(0);                 
                }               
            });
            
            try{
                tray.add(trayIcon);
            } catch (Exception e) {
                System.err.println("Can't add to tray");
            }
        } else {
            System.err.println("Tray unavailable");
        } 
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private static Image createImage(String path, String description) {
        URL imageURL = TimeTracker.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
