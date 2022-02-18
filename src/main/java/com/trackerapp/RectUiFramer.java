package com.trackerapp;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


public class RectUiFramer extends AnchorPane {
    private double positionX = 0;
    private double positionY = 0;
    private double width = 100;
    private double height = 100;
    private Button sizeControlButton = new Button("");
    private double sizeControlButtonWidth = App.width * 0.007;

    public RectUiFramer() {

        this.setPrefWidth(width);
        this.setPrefHeight(height);
        this.setStyle("-fx-border-color: #bfff96; -fx-border-width: 2");

//        Setup the size control button in the bottom right of the rectangle
        sizeControlButton.setMinHeight(0);
        sizeControlButton.setMinWidth(0);
        sizeControlButton.setPrefWidth(sizeControlButtonWidth);
        sizeControlButton.setPrefHeight(sizeControlButtonWidth);
        sizeControlButton.setAlignment(Pos.BOTTOM_RIGHT);
        sizeControlButton.setTranslateX(sizeControlButtonWidth / 2);
        sizeControlButton.setTranslateY(sizeControlButtonWidth / 2);
        AnchorPane.setBottomAnchor(sizeControlButton, 0d);
        AnchorPane.setRightAnchor(sizeControlButton, 0d);
        sizeControlButton.setStyle("fx-focus-color: firebrick; -fx-highlight-fill: transparent; -fx-background-color: rgba(196,239,148)");

        sizeControlButton.setOnMouseDragged(this::onResizeButtonDrag);
        this.setOnMouseDragged(this::onDrag);
        this.getChildren().add(sizeControlButton);

    }

    public void onResizeButtonDrag(MouseEvent event) {
        double initWidth = width;
        double initHeight = height;
        double posXCenter = positionX + width / 2;
        double posYCenter = positionY + height / 2;
        width =  (event.getSceneX()- posXCenter)*2;
        height = (event.getSceneY()- posYCenter)*2;
        double widthDelta = width - initWidth;
        double heightDelta = height - initHeight;
        positionX = positionX - (widthDelta)/2;
        positionY = positionY - (heightDelta)/2;

        if (event.getSceneX() > posXCenter){
            this.setLayoutX(positionX);
            this.setPrefWidth(width);
        }
        if (event.getSceneY() > posYCenter){
            this.setLayoutY(positionY);
            this.setPrefHeight(height);
        }


//        sizeControlButton.setLayoutX(event.getX());
//        sizeControlButton.setLayoutY(event.getY());
    }

    public void onDrag(MouseEvent event) {
        positionX = event.getSceneX() - width / 2;
        positionY = event.getSceneY() - height / 2;
        this.setLayoutX(positionX);
        this.setLayoutY(positionY);
    }

}
