package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.tracking.TrackerCSRT;

public class CustomTracker {
    private int trackerFrameNum=0;
    private final Rect[] boundingBoxes;
    private TrackerCSRT csrtTracker = TrackerCSRT.create();

    private final RectUiFramer trackerFrame;
    public CustomTracker(int videoLength, Rect initialRect){
        this.trackerFrame = new RectUiFramer(initialRect);

        boundingBoxes = new Rect[videoLength];
    }

    public void setBoundingBox(Mat image, Rect boundingBox, int frameNum){
        boundingBoxes[frameNum] = boundingBox;
    }

    public void initTracker(Mat image, int frameNum){
        csrtTracker = TrackerCSRT.create();
        Rect rectClone = trackerFrame.getRect().clone();
        boundingBoxes[frameNum] = rectClone;
        csrtTracker.init(image, rectClone);
        trackerFrameNum = frameNum;
        setTrackerUiFrame(frameNum);
    }

//    Updates the tracker from the last position to new one based on new frame.
    public boolean updateTracker(Mat image, int frameNum){
        if (frameNum != trackerFrameNum+1){
            System.out.println("Reinit");
            initTracker(image, frameNum);
        }
        Rect newPos = new Rect();
        boolean res = csrtTracker.update(image, newPos);
        if (res){
            boundingBoxes[frameNum] = newPos;
            trackerFrameNum = frameNum;
        }
        setTrackerUiFrame(frameNum);
        return res;
    }

    public void setTrackerUiFrame(int frameNum){
        Platform.runLater(()->{
            trackerFrame.setRect(boundingBoxes[frameNum].clone());
        });

    }
    public Pane getTrackerUI(){
        return trackerFrame;
    }
    public Rect getTrackerRect(int frameNum){
        return boundingBoxes[frameNum];
    }
    public Rect getFramerRect(){
        return trackerFrame.getRect();
    }



}
