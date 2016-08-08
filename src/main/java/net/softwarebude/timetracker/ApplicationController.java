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
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;
import net.softwarebude.basis.Packager;

/**
 * Controller for the GUI. It handles the interaction with the dialog.
 * @author Matthias Fischer
 */
public class ApplicationController implements Initializable {
    
    private final String pathFiles = "./files";
    private final String pathProjects = this.pathFiles + "/projectlist.txt";
    
    private Task task;
    private Settings settings;
    
    private ObservableList<Task> data;
    private ObservableList<String> autoProjects;
    
    private Calendar today;
    private Calendar selectedDay;
    
    @FXML private AnchorPane root;
    @FXML private DatePicker dpDate;
    
    @FXML private TextField tfStartTime;
    @FXML private TextField tfEndTime;
    @FXML private TextField tfProject;
    @FXML private TextField tfDescription;
    @FXML private Button btnStartStop;
    
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    
    @FXML private TableColumn tcStartTime;
    @FXML private TableColumn tcEndTime;
    @FXML private TableColumn tcProject;
    @FXML private TableColumn tcDescription;
    @FXML private TableView tvTasks;
    
    /**
     * Initialize the application xml and setup handling methods.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known
     * @param rb The resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.dpDate.setValue(LocalDate.now());
        this.today = this.selectedDay = Calendar.getInstance();
        
        this.data = FXCollections.observableArrayList();
        this.autoProjects = FXCollections.observableArrayList();
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);   
        
        this.tfStartTime.textProperty().addListener((observable, oldValue, newValue) -> { this.checkSaveAllowed(); });
        this.tfEndTime.textProperty().addListener((observable, oldValue, newValue) -> { this.checkSaveAllowed(); });
        this.tfProject.textProperty().addListener((observable, oldValue, newValue) -> { this.checkSaveAllowed(); });
        this.tfDescription.textProperty().addListener((observable, oldValue, newValue) -> { this.checkSaveAllowed(); });
        
        this.initFilesystem();
        this.settings = new Settings();
        this.settings.loadSettings();
        
        this.loadProjects();
        this.initTaskTable();
        this.loadTasks(this.today);
    } 
        
    /**
     * Handler for the start/stop button. It starts/stops the time recording for a task.
     * @param event ActionEvent
     */
    @FXML
    public void handleButtonStartStopAction(ActionEvent event) {
        
        if(this.task == null || !this.task.isStarted()) {
            if(!this.tfProject.getText().isEmpty() && !this.tfDescription.getText().isEmpty()) {
            
                this.btnStartStop.setText("Stop");
                this.task = new Task(this.tfProject.getText(), this.tfDescription.getText());
                this.task.start();
                
                this.tfStartTime.setText(this.task.getStartTime());
                this.tfEndTime.setText(this.task.getEndTime());
            }
        } else {
            
            this.btnStartStop.setText("Start");
            this.task.stop();
            this.data.add(this.task);
            
            EditTaskFiles.saveTasks(EditTaskFiles.getFilePath(this.selectedDay), this.data);
            this.addProject(this.task);
            
            this.tfStartTime.setText("");
            this.tfEndTime.setText("");
        }
    }
    
    /**
     * Handler for the button previous. It decrement the selected date. 
     * @param event ActionEvent
     */
    @FXML 
    public void handleButtonPreviousDate(ActionEvent event) {
        // -1 day
        this.selectedDay.setTimeInMillis(this.selectedDay.getTimeInMillis() - (1000 * 60 * 60 * 24));
        this.dpDate.setValue(this.dpDate.getValue().minusDays(1));
        this.handleDateChanged(null);
    }
    
    /**
     * Handler for the button next. It increment the selected date.
     * @param event ActionEvent
     */
    @FXML 
    public void handleButtonNextDate(ActionEvent event) {
        // +1 day
        this.selectedDay.setTimeInMillis(this.selectedDay.getTimeInMillis() + (1000 * 60 * 60 * 24));
        this.dpDate.setValue(this.dpDate.getValue().plusDays(1));
        this.handleDateChanged(null);
    }
    
    /**
     * Handler for date changed. It loads the tasks of the new date.
     * @param event ActionEvent
     */
    @FXML 
    public void handleDateChanged(ActionEvent event) {
        this.loadTasks(this.selectedDay);
        int tod = this.today.get(Calendar.DATE);
        int sel = this.selectedDay.get(Calendar.DATE);
        
        if(tod == sel) {
            this.btnStartStop.setDisable(true);
        } else {
            this.btnStartStop.setDisable(false);
        }
    }
    
    /**
     * Handler for the button save. It saves changes from an existent task.
     * @param event ActionEvent
     */
    @FXML
    public void handleButtonSave(ActionEvent event) {
        Task tmpTask = (Task)this.tvTasks.getSelectionModel().getSelectedItem();
        
        String pro = this.tfProject.getText();
        String desc = this.tfDescription.getText();
        String startTime = this.tfStartTime.getText();
        String endTime = this.tfEndTime.getText();
        
        if(!pro.equals("") && !desc.equals("") && !startTime.equals("") && !endTime.equals("")) {
            if(tmpTask != null) {
                tmpTask.setProject(pro);
                tmpTask.setDescription(desc);
            
                if(tmpTask.setStartTime(startTime) && tmpTask.setEndTime(endTime)) {
                    EditTaskFiles.saveTasks(EditTaskFiles.getFilePath(this.selectedDay), this.data);
                    this.loadTasks(this.selectedDay);
                }
            } else { // new Task
                tmpTask = new Task(pro, desc);
                tmpTask.setDateStart(this.selectedDay);
                tmpTask.setDateEnd(this.selectedDay);
                
                if(tmpTask.setStartTime(startTime) && tmpTask.setEndTime(endTime)) {
                    this.data.add(tmpTask);
            
                    EditTaskFiles.saveTasks(EditTaskFiles.getFilePath(this.selectedDay), this.data);
                    this.addProject(tmpTask);

                    this.tfStartTime.setText("");
                    this.tfEndTime.setText("");
                }
            }
        }
    }
    
    /**
     * Handler for the button delete task. It deletes the selected task.
     * @param event ActionEvent
     */
    @FXML
    public void handleButtonDelete(ActionEvent event) {
        Task t = (Task)this.tvTasks.getSelectionModel().getSelectedItem();
        if(t != null) {
            Dialog dia = new Dialog();
            dia.setTitle("Delete task");
            dia.setHeaderText("Delete task \"" + t.getProject() + ": " + t.getDescription() + "\"");
            dia.setContentText("Are you sure you want to delete the selected task?");
            
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            dia.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
            
            dia.setResultConverter(dialogButton -> {
                if (dialogButton == yesButton) {
                    this.data.remove(t);

                    EditTaskFiles.saveTasks(EditTaskFiles.getFilePath(this.selectedDay), this.data);
                    this.loadTasks(this.selectedDay);
                }
                return null;
            });
            
            dia.showAndWait();
        }
    }
    
    /**
     * Handler for the menuitem import. It triggers a import of backed up tasks.
     * @param event ActionEvent
     */
    @FXML
    public void handleMenuItemImport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TimeTracker", "*.tt"));
        fileChooser.setTitle("Import Backup");
        File importFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        
        if(importFile != null) {
            Packager.unzip(importFile, this.pathFiles);
            this.loadTasks(this.selectedDay);
            this.loadProjects();
        }
    }

    /**
     * Handler for the menuitem export. It triggers a backup of the tasks
     * @param event ActionEvent
     */
    @FXML
    public void handleMenuItemExport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TimeTracker", "*.tt"));
        fileChooser.setTitle("Export Backup");
        fileChooser.setInitialFileName(EditTaskFiles.getFilename(this.today) + "_backup");
        
        File exportFile = fileChooser.showSaveDialog(root.getScene().getWindow());
        if(exportFile != null) {
            Packager.zip(this.pathFiles, exportFile);
        }
    }
    
    /**
     * Handler for the menuitem settings. It opens a settingdialog.
     * @param event ActionEvent
     */
    @FXML
    public void handleMenuItemSettings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root, 250, 110));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handler fo the menuitem exit. It closes the application.
     * @param event ActionEvent
     */
    @FXML
    public void handleMenuItemExit(ActionEvent event) {
        System.exit(0);
    }
    
    /**
     * Handler for the menuitem about. Opens a dialog with informations about the application.
     * @param event ActionEvent
     */
    @FXML
    public void handleMenuItemAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TimeTracker");
        alert.setHeaderText("TimeTracker " + TimeTracker.VERSION);
        alert.setContentText("URL:\t\thttps://matze-fischer.de\nE-Mail:\tcontact@matze-fischer.de");
        alert.showAndWait();
    }
    
    /**
     * Handler for enter-pressed. It changes the focus to the next textfield.
     * @param event Actionevent
     */
    @FXML
    public void handleEnter(ActionEvent event) {
        if(event.getSource().equals(this.tfStartTime)) {
            this.tfEndTime.requestFocus();
        } else if(event.getSource().equals(this.tfEndTime)) {
            this.tfProject.requestFocus();
        } else if(event.getSource().equals(this.tfProject)) {
            this.tfDescription.requestFocus();
        } else if(event.getSource().equals(this.tfDescription)) {
            // do nothing
        }
    }
    
    /**
     * Check if the textfields are filled - if so the save function will be enabled
     */
    @FXML
    public void checkSaveAllowed() {
        String pro = this.tfProject.getText();
        String desc = this.tfDescription.getText();
        String startTime = this.tfStartTime.getText();
        String endTime = this.tfEndTime.getText();
        
        if(!pro.equals("") && !desc.equals("") && !startTime.equals("") && !endTime.equals("")) {
            this.btnSave.setDisable(false);
        } else {
            this.btnSave.setDisable(true);
        }
    }
    
    /**
     * Initialization of the table. It binds the keys of a task to the right column. 
     * Also it defines a selection-handler for the table.
     */
    private void initTaskTable() {
        this.tcStartTime.setCellValueFactory(new PropertyValueFactory<>("start"));
        this.tcEndTime.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.tcProject.setCellValueFactory(new PropertyValueFactory<>("project"));
        this.tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.tvTasks.setItems(this.data);
        
        this.tvTasks.getSelectionModel().selectedItemProperty().addListener((ObservableValue obs, Object oldSelection, Object newSelection) -> {
            if(newSelection != null) {
                tfProject.setText(((Task)newSelection).getProject());
                tfDescription.setText(((Task)newSelection).getDescription());
                tfStartTime.setText(((Task)newSelection).getStartTime());
                tfEndTime.setText(((Task)newSelection).getEndTime());
                btnDelete.setDisable(false);
                
            } else {
                tfProject.setText("");
                tfDescription.setText("");
                tfStartTime.setText("");
                tfEndTime.setText("");
                btnDelete.setDisable(true);
            }
        });  
    }
    
    /**
     * Create directory structure.
     */
    private void initFilesystem() {
        EditTaskFiles.createFile(EditTaskFiles.getDirectory(this.today), true);
        EditTaskFiles.createFile(this.pathProjects);
    } 
    
    /**
     * Loads tasks of the passed date. The tasks were directly saved into the table data.
     * @param date Date of tasks
     */
    private void loadTasks(Calendar date) {
        this.data.clear();
        
        List<String> lines = EditTaskFiles.readFile(EditTaskFiles.getFilePath(date));
        Task iss;

        for(int i=0;i<lines.size();i++) {
            iss = new Task(lines.get(i));
            this.data.add(iss);
        }
    }
    
    /**
     * Add a project to the project list. Only if its not already in the list.
     * @param task Task which contains the project description
     */
    private void addProject(Task task) {
        String pro = task.getProject();
        
        if(!this.autoProjects.contains(pro)) {
            this.autoProjects.add(pro);
            EditTaskFiles.saveFile(this.pathProjects, this.autoProjects);
        }
        
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);
    }
    
    /**
     * Load projects out of the projectlist file.
     */
    private void loadProjects() {
        this.autoProjects.clear();
        List<String> lines = EditTaskFiles.readFile(this.pathProjects);

        for(int i=0;i<lines.size();i++) {
            this.autoProjects.add(lines.get(i));
        }
        
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);
    }
}