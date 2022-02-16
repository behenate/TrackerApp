package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.tracking.TrackerCSRT;

public class CustomTracker {
    private Rect[] boundingBoxes;
    private TrackerCSRT csrtTracker = TrackerCSRT.create();
    private Pane trackerUI = new Pane();
    public CustomTracker(int videoLength){
        trackerUI.setStyle("-fx-background-color: rgba(54,215,118,0.39)");
        boundingBoxes = new Rect[videoLength];
    }

    public void setBoundingBox(Mat image, Rect boundingBox, int frameNum){
        boundingBoxes[frameNum] = boundingBox;
    }

    public void initTracker(Mat image, Rect boundingBox, int frameNum){
        boundingBoxes[frameNum] = boundingBox;
        csrtTracker.init(image, boundingBox);
        setTrackerUiFrame(frameNum);
    }

//    Updates the tracker from the last position to new one based on new frame.
    public boolean updateTracker(Mat image, int frameNum){
        Rect newPos = new Rect();
        boolean res = csrtTracker.update(image, newPos);
        if (res){
            boundingBoxes[frameNum] = newPos;
        }
        setTrackerUiFrame(frameNum);
        return res;
    }

    public void setTrackerUiFrame(int frameNum){
        Rect pos = boundingBoxes[frameNum];
        System.out.println(pos);
        Platform.runLater(()->{
            trackerUI.setTranslateX(pos.x);
            trackerUI.setTranslateY(pos.y);
            trackerUI.setMaxWidth(pos.width);
            trackerUI.setMaxHeight(pos.height);
            trackerUI.setMinWidth(pos.width);
            trackerUI.setMinHeight(pos.height);
        });

    }
    public Pane getTrackerUI(){
        return trackerUI;
    }



}
