package com.trackerapp;

import javafx.scene.input.MouseButton;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class TrackerManager implements SliderControllable{
    private final TrackingWindow trackingWindow;
    private final ArrayList<CustomTracker> trackers = new ArrayList<>();
    private final Video video;
    public TrackerManager(Video video){
        this.video = video;
        this.trackingWindow = new TrackingWindow(video);
        trackingWindow.setOnMouseClicked((event)->{
            if (event.getButton() == MouseButton.SECONDARY){
                addTracker(new CustomTracker(video.getLength(), new Rect(
                        (int) (event.getX() - Config.defaultTrackerWidth/2),
                        (int) (event.getY()-Config.defaultTrackerHeight/2),
                        Config.defaultTrackerWidth,
                        Config.defaultTrackerHeight)));
            }
        });
    }

    public void addTracker(CustomTracker tracker){
        tracker.initTracker(video.getVideoFrameReader().getFrameMat(), video.getVideoFrameReader().getCurrentFrameNum());
        trackers.add(tracker);
        trackingWindow.addTracker(tracker);
    }


//    Init all trackers with their current frame positions and image from video at current frame
    public void initAll(){
        Mat img = video.getVideoFrameReader().getFrameMat();
        for (CustomTracker tracker: trackers) {
            tracker.initTracker(img, video.frameNum());
        }
    }

    public void setPositionsToFrame(int frameNum){
        for (CustomTracker tracker: trackers) {
            tracker.setTrackerUiFrame(frameNum);
        }
    }

    public void trackForwardSingle(){
        initChanged();
        video.readAndDisplayNextFrame();
        Mat image = video.getVideoFrameReader().getFrameMat();
        for (CustomTracker tracker: trackers) {
            tracker.updateTracker(image, video.getVideoFrameReader().getCurrentFrameNum());
        }
    }


    public void trackAll(Mat image, int frameNum){
        for (CustomTracker tracker: trackers) {
            tracker.updateTracker(image, frameNum);
        }
    }


//    Functions that inits trackers that have been changed by the user
    public void initChanged(){
        for (CustomTracker tracker: trackers) {
            Rect trackerRect = tracker.getTrackerRect(video.frameNum());
            Rect frameRect = tracker.getFramerRect();
            System.out.println(trackerRect + "  " +  frameRect);
            if (!trackerRect.equals(frameRect)){
                tracker.initTracker(video.getVideoFrameReader().getFrameMat(), video.frameNum());
            }
        }
    }


    public ArrayList<CustomTracker> getTrackers(){
        return trackers;
    }


    public TrackingWindow getTrackingWindow(){
        return trackingWindow;
    }

    @Override
    public void onSliderUpdate(Number oldValue, Number newValue) {
        this.video.onSliderUpdate(oldValue, newValue);
        setPositionsToFrame(video.frameNum());
    }
}
