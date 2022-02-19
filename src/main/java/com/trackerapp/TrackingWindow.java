package com.trackerapp;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;


// Class to display all trackers over image
public class TrackingWindow extends Pane{
    private final Video video;


    public TrackingWindow(Video video){
        this.video = video;
        this.getChildren().add(video.getDisplay());
        VideoFrameReader frameReader = video.getVideoFrameReader();
    }

    public void addTracker(CustomTracker tracker){
        this.getChildren().add(tracker.getTrackerUI());
    }

    public void removeTracker(CustomTracker tracker){
        this.getChildren().remove(tracker);
    }

}
