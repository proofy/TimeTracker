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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.textfield.TextFields;

/**
 * Controller for the GUI. It handles the interaction with the dialog.
 * @author Matthias Fischer
 */
public class ApplicationController implements Initializable {
    
    private final String pathProjects = "./files/projectlist.txt";
    
    private boolean started;
    private Task task;
    private Settings settings;
    
    private ObservableList<Task> data;
    private ObservableList<String> autoProjects;
    
    private Calendar today;
    private Calendar selectedDay;
    
    @FXML private DatePicker dpDate;
    
    @FXML private Button btnStartStop;
    @FXML private TextField tfProject;
    @FXML private TextField tfDescription;
    
    @FXML private TableColumn tcFrom;
    @FXML private TableColumn tcTo;
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
        this.started = false;
        
        this.settings = new Settings();
        
        this.dpDate.setValue(LocalDate.now());
        this.today = this.selectedDay = Calendar.getInstance();
        
        this.data = FXCollections.observableArrayList();
        this.autoProjects = FXCollections.observableArrayList();
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);       
        
        this.initFilesystem();
        
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
        
        if(!this.started) {
            if(!this.tfProject.getText().isEmpty() && !this.tfDescription.getText().isEmpty()) {
            
                this.btnStartStop.setText("Stop");
                this.task = new Task(this.tfProject.getText(), this.tfDescription.getText());
                this.task.start();
                this.started = true;
            }
        } else {
            
            this.btnStartStop.setText("Start");
            this.started = false;
            this.task.stop();
            this.data.add(this.task);
            
            EditFiles.saveTasks(EditFiles.getFilePath(this.selectedDay), this.data);
            this.addProject(this.task);
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
    }
    
    /**
     * Handler for the button save. It saves changes from an existent task.
     * @param event ActionEvent
     */
    @FXML
    public void handleButtonSave(ActionEvent event) {
        Task iss = (Task)this.tvTasks.getSelectionModel().getSelectedItem();
        String pro = this.tfProject.getText();
        String desc = this.tfDescription.getText();
        
        if(iss != null && !pro.equals("") && !desc.equals("")) {
            iss.setProject(pro);
            iss.setDescription(desc);
            EditFiles.saveTasks(EditFiles.getFilePath(this.selectedDay), this.data);
            this.loadTasks(this.selectedDay);
        }
    }
    /**
     * Handler for the button delete task. It deletes the selected task.
     * @param event ActionEvent
     */
    @FXML
    public void handleButtonDelete(ActionEvent event) {
        Task i = (Task)this.tvTasks.getSelectionModel().getSelectedItem();
        if(i != null) {
            Dialog dia = new Dialog();
            dia.setTitle("Delete task");
            dia.setHeaderText("Delete task \"" + i.getProject() + ": " + i.getDescription() + "\"");
            dia.setContentText("Are you sure you want to delete the selected task?");
            
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            dia.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
            
            dia.setResultConverter(dialogButton -> {
                if (dialogButton == yesButton) {
                    this.data.remove(i);

                    EditFiles.saveTasks(EditFiles.getDirectory(this.selectedDay), this.data);
                    this.loadTasks(this.selectedDay);
                }
                return null;
            });
            
            dia.showAndWait();
        }
    }
    
    /**
     * Initialization of the table. It binds the keys of a task to the right column. 
     * Also it defines a selection-handler for the table.
     */
    private void initTaskTable() {
        this.tcFrom.setCellValueFactory(new PropertyValueFactory<>("start"));
        this.tcTo.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.tcProject.setCellValueFactory(new PropertyValueFactory<>("project"));
        this.tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.tvTasks.setItems(this.data);
        
        this.tvTasks.getSelectionModel().selectedItemProperty().addListener((ObservableValue obs, Object oldSelection, Object newSelection) -> {
            if(newSelection != null) {
                tfProject.setText(((Task)newSelection).getProject());
                tfDescription.setText(((Task)newSelection).getDescription());
            } else {
                tfProject.setText("");
                tfDescription.setText("");
            }
        });  
    }
    
    /**
     * Create directory structure.
     */
    private void initFilesystem() {
        EditFiles.createFile(EditFiles.getDirectory(this.today), true);
        EditFiles.createFile(this.pathProjects);
    } 
    
    /**
     * Loads tasks of the passed date. The tasks were directly saved into the table data.
     * @param date Date of tasks
     */
    private void loadTasks(Calendar date) {
        this.data.clear();
        
        List<String> lines = EditFiles.readFile(EditFiles.getFilePath(date));
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
            EditFiles.saveFile(this.pathProjects, this.autoProjects);
        }
        
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);
    }
    
    /**
     * Load projects out of the projectlist file.
     */
    private void loadProjects() {
        this.autoProjects.clear();
        List<String> lines = EditFiles.readFile(this.pathProjects);

        for(int i=0;i<lines.size();i++) {
            this.autoProjects.add(lines.get(i));
        }
        
        TextFields.bindAutoCompletion(this.tfProject, this.autoProjects);
    }
}