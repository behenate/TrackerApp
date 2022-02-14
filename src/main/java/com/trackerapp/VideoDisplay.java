package com.trackerapp;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class VideoDisplay extends Pane {

    private final VideoFrameReader frameReader;

    private final ImageView videoView = new ImageView();
    private Thread playThread;

    //    Constructor which uses custom width and height
    public VideoDisplay(VideoFrameReader frameReader, double videoWidth, double videoHeight){
        this(frameReader);
        //    Set default video width and height
        this.setPrefWidth(videoWidth);
        this.setPrefHeight(videoHeight);
        videoView.setFitWidth(videoWidth);
        videoView.setFitHeight(videoHeight);
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
