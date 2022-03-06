package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class TrackerManager implements SliderControllable{
    private final TrackingWindow trackingWindow;
    private final ArrayList<CustomTracker> trackers = new ArrayList<>();
    private final Video video;
    private Thread trackingThread;

    public TrackerManager(Video video){
        this.video = video;
        this.trackingWindow = new TrackingWindow(video);
        trackingWindow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
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
        video.getVideoFrameReader().getCurrentFrameNum();
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


    //    Tracks all selected trackers (currently just all trackers)
    public boolean trackSelectedSingle(int frameJump){
        boolean tracksSuccessful = true;
        initChanged();
        int frameNum = video.frameNum();
        video.readAndDisplayFrame(frameNum + frameJump);
        Mat image = video.getVideoFrameReader().getFrameMat();
        for (CustomTracker tracker: trackers) {
            tracksSuccessful = tracksSuccessful && tracker.updateTracker(image, video.frameNum());
        }
        return tracksSuccessful;
    }

    public boolean trackForwardSingle(){
        return trackSelectedSingle(1);
    }
    public boolean trackBackwardSingle(){
        return trackSelectedSingle(-1);
    }

    public void trackTillEnd(int frameJump){
        if (trackingThread != null && trackingThread.isAlive()){
            trackingThread.interrupt();
        }
        trackingThread = new Thread(() -> {
            while (video.frameNum()+frameJump < video.getLength() && video.frameNum()+frameJump >= 0){
                if (!trackSelectedSingle(frameJump) || Thread.currentThread().isInterrupted()){
                    break;
                };
            }
        }
        );
        trackingThread.start();
    }

    public void trackForwardTillEnd(){
        trackTillEnd(1);

    }

    public void trackBackwardTillFail(){
        trackTillEnd(-1);
    }

    public void stopTracking(){
        if (trackingThread != null){
            trackingThread.interrupt();
        }
    }



//    Functions that inits trackers that have been changed by the user
    public void initChanged(){
        for (CustomTracker tracker: trackers) {
            Rect trackerRect = tracker.getTrackerRect(video.frameNum());
            Rect frameRect = tracker.getFramerRect();

            if (trackerRect == null || !trackerRect.equals(frameRect)){
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
        new Thread(()->{
            video.readAndDisplayFrame(newValue.intValue());
            setPositionsToFrame(newValue.intValue());
        }).start();

    }
}
