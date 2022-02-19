package com.trackerapp;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.opencv.core.Rect;


public class RectUiFramer extends AnchorPane {
    private Rect rect;

    public RectUiFramer(Rect initialRect) {
        double sizeControlButtonWidth = App.width * 0.007;
        rect = initialRect;

        updateLayout();

        this.setStyle("-fx-border-color: #bfff96; -fx-border-width: 2");

//        Setup the size control button in the bottom right of the rectangle
        Button sizeControlButton = new Button("");
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

//    On drag of the square resize the window
    public void onResizeButtonDrag(MouseEvent event) {
        Rect initRect = rect.clone();
        double posXCenter = rect.x + rect.width / 2;
        double posYCenter = rect.y + rect.height / 2;
        rect.width = (int) ((event.getSceneX()- posXCenter)*2);
        rect.height = (int) ((event.getSceneY()- posYCenter)*2);
        double widthDelta = rect.width - initRect.width;
        double heightDelta = rect.height - initRect.height;
        rect.x = (int) (rect.x - (widthDelta)/2);
        rect.y = (int) (rect.y - (heightDelta)/2);

        if (event.getSceneX() > initRect.x+15){
            this.setLayoutX(rect.x);
            this.setPrefWidth(rect.width);
        }
        if (event.getSceneY() > initRect.y+15){
            this.setLayoutY(rect.y);
            this.setPrefHeight(rect.height);
        }
    }

//    On drag of the entire frame move it.
    public void onDrag(MouseEvent event) {
        rect.x = (int) (event.getSceneX() - rect.width / 2);
        rect.y = (int) (event.getSceneY() - rect.height / 2);
        updateLayout();
    }

    public Rect getRect(){
        return rect;
    }

    public void setRect(Rect rect){
        this.rect = rect;
        updateLayout();
    }

    private void updateLayout(){
        this.setLayoutX(rect.x);
        this.setLayoutY(rect.y);
        this.setPrefWidth(rect.width);
        this.setPrefHeight(rect.height);
    }
}
