package com.trackerapp;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


//  Class that represents the triangle that can be slided to set video frame
class FrameIndicator extends Pane {

    protected double lastDragPos = 0;
    protected double lastDragMousePos = -1;

    public FrameIndicator() {
        ImageView triangleImage = new ImageView(new Image("triangle.png"));
        triangleImage.setFitWidth(20);
        triangleImage.setPreserveRatio(true);

        Pane indicatorLine = new Pane();
        indicatorLine.setStyle("-fx-background-color: black");
        indicatorLine.setMaxWidth(0.5);
        indicatorLine.setPrefHeight(100);
        indicatorLine.setTranslateX(-0.5);
        indicatorLine.setTranslateY(-2);

        this.setMaxWidth(20);
        VBox indicator = new VBox();
        indicator.getChildren().addAll(triangleImage, indicatorLine);
        indicator.setAlignment(Pos.CENTER);
        this.getChildren().add(indicator);
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onDrag);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseUp);
    }

    protected void onDrag(MouseEvent event) {
        if (lastDragMousePos == -1) {
            lastDragMousePos = event.getSceneX();
            return;
        }
        this.setTranslateX(lastDragPos + (event.getSceneX() - lastDragMousePos));
    }

    protected void onMouseUp(MouseEvent event) {
        lastDragPos = this.getTranslateX();
        lastDragMousePos = -1;
    }

    public double getCenteredPos() {
        return this.getTranslateX() + this.getWidth() / 2;
    }
}

//  Class that represents a video bar on the slider
class VideoBar extends Pane {
    protected double lastDragPos = 0;
    public double lastDragMousePos = -1;
}

public class SliderWindow extends StackPane {
    private final SliderControllable controllable;
    FrameIndicator frameIndicator = new FrameIndicator();
    private final VideoBar videoBar = new VideoBar();

    private double controllablePosX = 0;
    private double zoom = 1;

    private final double minValue;
    private final double maxValue;
    private double currentValue=0;

    public SliderWindow(SliderControllable controllable, double minValue, double maxValue) {
        this.controllable = controllable;
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.setStyle("-fx-background-color: #7f7f7f;-fx-border-color: #c3c3c3;" +
                " -fx-border-width: 2px");
        this.setMaxWidth(App.width);
        this.setAlignment(Pos.CENTER_LEFT);

        videoBar.setStyle("-fx-background-color: #4697a2; -fx-border-color: #0f4a53; -fx-background-radius: 18 18 18 18; -fx-border-radius: 18 18 18 18");
        videoBar.setMaxHeight(20);

        this.getChildren().add(videoBar);

        frameIndicator.setLayoutX(0);
        frameIndicator.setLayoutY(0);
        this.getChildren().add(frameIndicator);

        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseUp);
        this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
    }

    private void forceControllableWidth(double value) {
        videoBar.setMinWidth(value);
        videoBar.setMaxWidth(value);
    }

    //    On drag move the video indicator
    private void onMouseDrag(MouseEvent event) {

        if (videoBar.lastDragMousePos == -1) {
            videoBar.lastDragMousePos = event.getSceneX();
            return;
        }
        //        Check if the click wasn't on the indicator, if so return
        if (frameIndicator.lastDragMousePos == -1) {
            double positionDelta = (event.getSceneX() - videoBar.lastDragMousePos);
            controllablePosX = videoBar.lastDragPos + positionDelta;
            double frameIndicatorPosX = frameIndicator.lastDragPos + positionDelta;
            videoBar.setTranslateX(controllablePosX);
            frameIndicator.setTranslateX(frameIndicatorPosX);
        }

        double newValue = calculateCurrentValue();
        if (currentValue != newValue){
            controllable.onSliderUpdate(currentValue, newValue);
            currentValue = newValue;
        }
    }

    //    Reset drag stats
    private void onMouseUp(MouseEvent event) {
        videoBar.lastDragPos = controllablePosX;
        videoBar.lastDragMousePos = -1;
        frameIndicator.onMouseUp(event);
    }

    private void onScroll(ScrollEvent event) {
        double newZoom = zoom + event.getDeltaY() / 400;
        if (newZoom > 0.1 && newZoom < 3) {
            zoom = newZoom;
            forceControllableWidth(App.width * zoom);
            videoBar.setPrefHeight(20 + (20 * zoom) / 10);
        }
    }

    private double calculateCurrentValue() {
        double indicatorPos = frameIndicator.getCenteredPos();
        double barPos = videoBar.getTranslateX();
        double barWidth = videoBar.getWidth();
        double percentage = (indicatorPos - barPos) / barWidth;
        double newValue_ = minValue +  (maxValue - minValue)*percentage;
        newValue_ = Math.max(minValue, newValue_);
        newValue_ = Math.min(maxValue, newValue_);
        return newValue_;
    }
}
