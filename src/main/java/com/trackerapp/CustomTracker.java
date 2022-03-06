package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.tracking.TrackerCSRT;

import java.util.concurrent.Semaphore;

public class CustomTracker {
    private final Rect[] boundingBoxes;
    private TrackerCSRT csrtTracker = TrackerCSRT.create();

    private final RectUiFramer trackerFrame;

    //    boolean that changes to false when the video has been scrolled.
    private boolean onLastTrackedFrame = true;

    public CustomTracker(int videoLength, Rect initialRect) {
        this.trackerFrame = new RectUiFramer(initialRect.clone());
        boundingBoxes = new Rect[videoLength+1];
    }

    public void setBoundingBox(Mat image, Rect boundingBox, int frameNum) {
        boundingBoxes[frameNum] = boundingBox;
    }

//    Initializes the tracker at the provided frame number
    public void initTracker(Mat image, int frameNum) {
        csrtTracker = TrackerCSRT.create();
        boundingBoxes[frameNum] = trackerFrame.getRect().clone();
        csrtTracker.init(image, trackerFrame.getRect().clone());
        setTrackerUiFrame(frameNum);
    }

    //    Updates the tracker from the last position to new one based on new frame.
    public boolean updateTracker(Mat image, int frameNum) {
        if (!onLastTrackedFrame) {
            initTracker(image, frameNum);
        }
        Rect newPos = new Rect();
        boolean res = csrtTracker.update(image, newPos);
        if (res) {
            boundingBoxes[frameNum] = newPos;
            setTrackerUiFrame(frameNum);
            onLastTrackedFrame = true;
        }
        return res;
    }

    public void setTrackerUiFrame(int frameNum) {
        // Only updateTracker resets that to true, any change from outside will leave it at false which will reinitialize the tracker
        onLastTrackedFrame = false;
        if (boundingBoxes[frameNum] != null)
            trackerFrame.setRect(boundingBoxes[frameNum].clone());
    }

    public Pane getTrackerUI() {
        return trackerFrame;
    }

    public Rect getTrackerRect(int frameNum) {
        return boundingBoxes[frameNum];
    }

    public Rect getFramerRect() {
        return trackerFrame.getRect();
    }
}
