package com.trackerapp;

import javafx.application.Application;

// Wrapper class for the javafx app, cant use module-info, because it doesnt work with open-cv, and
public class AppWrapper {
    public static void main(String[] args){
        Application.launch(App.class, args);
    }
}
