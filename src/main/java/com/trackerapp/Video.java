package com.trackerapp;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.io.FileUtils;


public class Video {
    private String videoPath;
    private String videoFilename;

    private String imagesDirPath;
    private File imagesDir;

    private ImageView videoView = new ImageView();

    //    Set default video width and height
    private double videoWidth=1280;
    private double videoHeight=720;

    private VideoDisplay videoDisplay;

    private VideoFrameReader frameReader;

    //    Constructor which uses custom width and height
    public Video(String videoPath, double videoWidth, double videoHeight){
        this(videoPath);
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoDisplay = new VideoDisplay(frameReader, videoWidth, videoHeight);



    }

    //  Constructor which uses default width and height
    public Video(String path){
        videoPath = path;
        //         Find name of the provided video file
        String[] filenameSplit = videoPath.split("/");
        videoFilename = filenameSplit[filenameSplit.length-1];

//          Generate path for the images
        imagesDirPath = "src/main/resources/" + videoFilename + "/";
//        Create directory for images, if directory already exists, the application assumes that the file has already been converted and will ask if it should convert again
        imagesDir = new File(imagesDirPath);
        this.frameReader = new VideoFrameReader(videoPath, imagesDir);
    }

//    Constructors for webcam use
    public Video(){
        frameReader = new VideoFrameReader();
    }
    public Video(int captureWidth, int captureHeight){
        this();
        frameReader.setCaptureResolution(captureWidth, captureHeight);
    }


    public void convertToImageSequence(String extension, double width, double height) throws IOException {
        if (imagesDir.exists()){
            Alert existsAlert = new Alert(Alert.AlertType.CONFIRMATION);
            existsAlert.setTitle("File already converted!");
            existsAlert.setHeaderText("File already converted!");
            existsAlert.setContentText("The file has already been converted. Do you want to convert again with current settings?");
            Optional<ButtonType> result = existsAlert.showAndWait();
            if (result.get() == ButtonType.CANCEL){
                return;
            };
        }else {
            imagesDir.mkdir();
        }
        FileUtils.cleanDirectory(imagesDir);

        Thread conversionThread = new Thread(() -> {
            int frameNum = 0;
            frameReader.readFrame(frameNum);
            while (!frameReader.frameMatEmpty()) {
                videoDisplay.displayCurrentFrame();
                Mat resized = new Mat();
                Size sz = new Size(width, height);
                Imgproc.resize(frameReader.getFrameMat(), resized, sz);
                Imgcodecs.imwrite(imagesDirPath + frameNum + extension, resized);
                frameNum++;
                frameReader.readFrame(frameNum);
            }
        });
        conversionThread.start();
    }


    public void removeConvertedImageSequence() throws IOException {
        String filepath = "src/main/resources/" + videoFilename + "/";
        File directory = new File(filepath);
        FileUtils.cleanDirectory(directory);
        directory.delete();
    }


    public boolean hasBeenConverted(){
        return imagesDir.exists();
    }

    public Node getDisplay(){
        return videoDisplay;
    }
}
