package com.trackerapp;

import javafx.scene.image.Image;
import jdk.jshell.execution.Util;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;

public class VideoFrameReader {
    private final String videoFilepath;
    private final File imagesDir;
    private int currentFrameNum = 0;
    private VideoCapture videoCapture;
    private Mat frameMat = null;
    private Image frameImage = null;
    private int videoLength;

    private double videoWidth;
    private double videoHeight;

    private Size videoSize;

//    Constructor with provided resizing
    public VideoFrameReader(String videoFilepath, File imagesDir, double videoWidth, double videoHeight){
        this(videoFilepath, imagesDir);
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoSize = new Size(videoWidth, videoHeight);
    }

//  Constructor with default size from the video
    public VideoFrameReader(String videoFilepath, File imagesDir){
        this.videoCapture = new VideoCapture(videoFilepath);
        this.videoFilepath = videoFilepath;
        this.imagesDir = imagesDir;
        this.videoLength = (int) this.videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
        this.videoWidth = (int) this.videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        this.videoHeight = this.videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        this.videoSize = new Size(videoWidth, videoHeight);
    }
//    For webcam
    public VideoFrameReader(){
        this.videoCapture = new VideoCapture(0);
        this.videoLength = Integer.MAX_VALUE;
        videoFilepath = "/knjjbgfcvhfvbnmkugfvbnjgfvb";
        imagesDir = new File(videoFilepath);
        this.videoWidth = (int) this.videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        this.videoHeight = this.videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
    }

//    Loads frame with the provided number
    public void readFrame(int frameNum){
        if (frameNum > videoLength){
            return;
        }
        String frameFilepath = imagesDir.getPath() + "/" + frameNum;
//        Load image from images if exists, kinda ugly but no point in writing a function for it
        if (new File(frameFilepath +".jpg").exists()){
            frameMat = Imgcodecs.imread(frameFilepath+".jpg");
        }else if (new File(frameFilepath +".png").exists()){
            frameMat = Imgcodecs.imread(frameFilepath+".png");
        }else{
//            Use set only if the app tries to read frame different than the current one, since set on frame number is slow.
            if (frameNum != (int) videoCapture.get(Videoio.CAP_PROP_POS_FRAMES)+1){
                videoCapture.set(1, frameNum-1);
            }
            Mat newMat = new Mat();
            videoCapture.read(newMat);
            if (newMat.empty()){
                return;
            }
            frameMat = newMat;
        }
//        Resize the frame to desired size
        if (!frameMat.size().equals(videoSize)){
            Imgproc.resize(frameMat, frameMat, videoSize);
        }

        frameImage = Utils.matToImg(".bmp", frameMat);
        currentFrameNum = frameNum;
    }

    public Image getFrameImage(){
        return frameImage;
    }
    public Mat getFrameMat(){
        return frameMat;
    }

    public int getCurrentFrameNum(){
        return currentFrameNum;
    }

    public void setSize(int width, int height){
        this.videoWidth = width;
        this.videoHeight = height;
        this.videoSize = new Size(width, height);
    }

    //    Sets capture resolution (useful for webcam use, doesn't work with video)
    public void setCaptureResolution(int width, int height){
        videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
        videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
    }

    public boolean frameMatEmpty(){
        return frameMat.empty();
    }

    public int getVideoLength(){
        return videoLength;
    }

}
