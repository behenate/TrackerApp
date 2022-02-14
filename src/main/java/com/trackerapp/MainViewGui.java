package com.trackerapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;

public class MainViewGui extends AnchorPane {
    private double windowWidth = App.width;
    private double windowHeight = App.height;
    private final App appInstance;
    //        Create and position video viwe
    Video video = new Video("/home/wojciech/IdeaProjects/TrackerApp/src/main/resources/videos/dino.mp4",
            windowWidth*0.6f, windowWidth*0.6f*0.5625f);
//    Video video = new Video(320, 24-)
    Popup conversionPopup;
    public MainViewGui(App appInstance){
        this.appInstance = appInstance;

        AnchorPane.setTopAnchor(video.getDisplay(), windowHeight/20);
        AnchorPane.setLeftAnchor(video.getDisplay(), windowWidth/20);
        this.getChildren().add(video.getDisplay());

//        Create and positon Slider
        FrameSlider slider = new FrameSlider(video);
        slider.setPrefWidth(App.width-App.width/5);
        AnchorPane.setBottomAnchor(slider, windowHeight/20);
        this.getChildren().add(slider);


        //        Container for conversion buttons
        HBox conversionBox = new HBox();

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
        AnchorPane.setRightAnchor(conversionBox, windowWidth/34);
        AnchorPane.setTopAnchor(conversionBox, windowHeight/20);
        this.getChildren().add(conversionBox);


        convertButton.setOnAction((e)->{
            if (conversionPopup==null)
                conversionPopup = createConversionPopup(()->clearButton.setVisible(true));
            conversionPopup.setX(appInstance.getStage().getX()+convertButton.getLayoutX());
            conversionPopup.setY(appInstance.getStage().getY()+convertButton.getLayoutY()+convertButton.getHeight()+40);
            conversionPopup.show(appInstance.getStage());
        });
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
