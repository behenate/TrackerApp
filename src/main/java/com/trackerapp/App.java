package com.trackerapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class App extends Application {
    public static double width = 1600;
    public static double height = 900;
    private Stage stage;
    @Override
    public void start(Stage stage){
        this.stage = stage;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        MainViewGui mainViewGui = new MainViewGui(this);
        Scene scene = new Scene(mainViewGui, width, height);
        stage.setTitle("TrackerApp");
        stage.setScene(scene);
        stage.show();
    }
    public Stage getStage(){
        return stage;
    }
    public static void main(String[] args) {
        launch();
    }
}