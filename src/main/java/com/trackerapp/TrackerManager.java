package com.trackerapp;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class TrackerManager {
    private int currentFrameNum = 0;

    private ArrayList<CustomTracker> trackers = new ArrayList<>();
    public TrackerManager(){
        CustomTracker testTracker = new CustomTracker(217);
    }

    public void addTracker(CustomTracker tracker){
        trackers.add(tracker);

    }
    public void initAll(Rect[] rects, Mat image, int frameNum){
        Rect testTrackerStart = new Rect(100, 130, 578,388);
        for (int i = 0; i < trackers.size(); i++) {
            CustomTracker tracker = trackers.get(i);
            tracker.initTracker(image, rects[i], currentFrameNum);
        }
        currentFrameNum = frameNum;
    }
    public void trackAll(Mat image, int frameNum){
        for (CustomTracker tracker: trackers) {
            tracker.updateTracker(image, frameNum);
        }
    }
    public ArrayList<CustomTracker> getTrackers(){
        return trackers;
    }

}
