package com.trackerapp;

import javafx.scene.layout.Pane;
import org.opencv.core.Rect;

// Class to display all trackers over image
public class TrackingWindow extends Pane implements SliderControllable{
    private Video video;
    private TrackerManager trackerManager = new TrackerManager();
    public TrackingWindow(Video video){
        this.video = video;
        this.getChildren().add(video.getDisplay());

        trackerManager.addTracker(new CustomTracker(video.getLength()));
        trackerManager.initAll(
                new Rect[]{new Rect(150, 150, 478, 288)},
                video.getVideoFrameReader().getFrameMat(),
                video.getVideoFrameReader().getCurrentFrameNum()
        );
        VideoFrameReader frameReader = video.getVideoFrameReader();
        drawTrackers();
        new Thread(()->{
            while (!frameReader.frameMatEmpty()){
                video.readAndDisplayNextFrame();
                trackerManager.trackAll(frameReader.getFrameMat(), frameReader.getCurrentFrameNum());
            }
        }).start();
        this.getChildren().add(new RectUiFramer());

    }


    @Override
    public void onSliderUpdate(Number oldValue, Number newValue) {
        video.onSliderUpdate(oldValue, newValue);
    }
    public void drawTrackers(){
        for (CustomTracker tracker : trackerManager.getTrackers()) {
            this.getChildren().add(tracker.getTrackerUI());
        }
    }
}
