package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import static com.trackerapp.Utils.matToImg;


public class VideoDisplay extends Pane {

    private VideoFrameReader frameReader;

    private ImageView videoView = new ImageView();
    private Thread playThread;

//    Set default video width and height
    private double videoWidth=1280;
    private double videoHeight=720;

//    Constructor which uses custom width and height
    public VideoDisplay(VideoFrameReader frameReader, double videoWidth, double videoHeight){
        this(frameReader);
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.setPrefWidth(videoWidth);
        this.setPrefHeight(videoHeight);
        this.getChildren().add(videoView);
    }

//  Constructor which uses default width and height
    public VideoDisplay(VideoFrameReader frameReader){
        this.frameReader = frameReader;
    }


    public void play(){
        play(33);
    }


    public void play(int waitTime){
        if (playThread != null && playThread.isAlive()){
            playThread.interrupt();
        }
        playThread = new Thread(()->{
            for (int i = 0; i < frameReader.getVideoLength(); i++) {
                try {
                    frameReader.readFrame(i);
                    displayCurrentFrame();
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        playThread.start();
    }

    public void pause(){
        playThread.interrupt();
    }


    public void displayCurrentFrame(){
        Platform.runLater(()->videoView.setImage(frameReader.getFrameImage()));
    }

}
