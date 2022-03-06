package com.trackerapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;

public class MainViewGui extends VBox{
    private double windowWidth = App.width;
    private double windowHeight = App.height;
    private final App appInstance;
    //        Create and position video
    private final Video video = new Video("/home/wojciech/IdeaProjects/TrackerApp/src/main/resources/videos/dino.mp4",
            1280, 720);

    private final TrackerManager trackerManager = new TrackerManager(video);

    Popup conversionPopup;
    public MainViewGui(App appInstance){
//        ESSENTIAL BOXES
        VBox miscBox = new VBox();
        HBox controlsBox = new HBox();
        VBox sliderBox = new VBox();
        HBox conversionBox = new HBox();


        this.appInstance = appInstance;



//        SLIDER

//        Create and position Slider
        FrameSlider slider = new FrameSlider(trackerManager);
        slider.setPrefWidth(App.width-App.width/5);
        sliderBox.getChildren().add(slider);

//        CONVERSION

//        Button for conversion
        Button convertButton = new Button("Convert To Image Sequence");
        conversionBox.getChildren().add(convertButton);

//        Optional button for clearing conversion files
        Button clearButton = new Button("Clear converted files");
        clearButton.setVisible(false);
        clearButton.setOnAction((event)->{
            try {
                video.removeConvertedImageSequence();
                clearButton.setVisible(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        conversionBox.getChildren().add(clearButton);
        if (video.hasBeenConverted())
            clearButton.setVisible(true);


        convertButton.setOnAction((e)->{
            if (conversionPopup==null)
                conversionPopup = createConversionPopup(()->clearButton.setVisible(true));
            conversionPopup.setX(appInstance.getStage().getX()+convertButton.getLayoutX());
            conversionPopup.setY(appInstance.getStage().getY()+convertButton.getLayoutY()+convertButton.getHeight()+40);
            conversionPopup.show(appInstance.getStage());
        });

//        TRACKING CONTROLS
        controlsBox.setPadding(new Insets(App.height*0.02,50,App.height*0.02,50));
        controlsBox.setSpacing(App.width/100);

        Button trackBackwardTillFailButton = new Button("<=");
        trackBackwardTillFailButton.setOnAction(event->{
            trackerManager.trackBackwardTillFail();
        });

        Button singleTrackBackwardButton = new Button("<");
        singleTrackBackwardButton.setOnAction(event -> {
            trackerManager.trackBackwardSingle();
        });

        Button stopTrackingButton = new Button("⬜" );
        stopTrackingButton.setOnAction(event -> {
            trackerManager.stopTracking();
        });

        Button singleTrackForwardButton = new Button(">");
        singleTrackForwardButton.setOnAction((event)->{
            trackerManager.trackForwardSingle();
        });

        Button trackForwardTillFailButton = new Button("=>");
        trackForwardTillFailButton.setOnAction(event->{
            trackerManager.trackForwardTillEnd();
        });

        controlsBox.getChildren().addAll(
                trackBackwardTillFailButton, singleTrackBackwardButton, stopTrackingButton,
                singleTrackForwardButton, trackForwardTillFailButton
        );
//        Set the width of the control buttons
        for (Node elem:controlsBox.getChildren()) {
            if (elem instanceof Button){
                ((Button) elem).setPrefWidth(50);
            }
        }

//        FINAL LAYOUT
//        Pack the elements
        controlsBox.setAlignment(Pos.CENTER);

        sliderBox.setAlignment(Pos.BOTTOM_CENTER);
        miscBox.setAlignment(Pos.TOP_CENTER);
        miscBox.getChildren().add(conversionBox);

        SliderWindow sliderWindow = new SliderWindow(trackerManager,0, video.getLength());
        sliderWindow.setPrefHeight(App.height*0.13);
        sliderWindow.setMaxWidth(App.width*0.8);

        this.getChildren().addAll(new HBox(trackerManager.getTrackingWindow(), miscBox) , controlsBox, slider, sliderWindow);
    }


    private Popup createConversionPopup(Runnable onStart){
        Popup conversionPopup = new Popup();
        VBox conversionPopupBox = new VBox();
        conversionPopupBox.setStyle("-fx-background-color: #FFFFFF");
//        Create UI elements
        Label widthLabel = new Label("Image conversion width");
        TextField widthTextField = new TextField("1280");
        Label heightLabel = new Label("Image conversion height: ");
        TextField heightTextField = new TextField("720");
        Button convertButton = new Button("Convert");
        Button cancelButton = new Button("Cancel");
        HBox buttonsBox = new HBox(convertButton, cancelButton);

        ObservableList<String> conversionFormat =
                FXCollections.observableArrayList(
                        ".bmp",
                        ".jpg"
                );
        final ComboBox conversionFormatBox = new ComboBox(conversionFormat);
        conversionFormatBox.setValue(".bmp");

//        Pack elements
        conversionPopupBox.getChildren().addAll(widthLabel,widthTextField, heightLabel, heightTextField, conversionFormatBox, buttonsBox);
        conversionPopup.getContent().add(conversionPopupBox);

//        Link functionality
        convertButton.setOnAction((event) -> {
            double conversionWidth = Double.parseDouble(widthTextField.getText());
            double conversionHeight = Double.parseDouble(heightTextField.getText());
            conversionPopup.hide();
            conversionPopup.setHideOnEscape(true);
            try {
                video.convertToImageSequence(conversionFormatBox.getValue().toString(), conversionWidth, conversionHeight);
                onStart.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction((event -> conversionPopup.hide()));
        return conversionPopup;
    }

}
