package com.trackerapp;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;

public class Utils {
    public static Image matToImg(String extension, Mat mat, double width, double height){
        Imgproc.resize(mat, mat, new Size(width, height));
        return matToImg(extension, mat);
    }


    public static Image matToImg(String extension, Mat mat){
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(extension, mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }
}
