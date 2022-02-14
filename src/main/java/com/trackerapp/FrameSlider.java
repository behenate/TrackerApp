package com.trackerapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;

public class FrameSlider extends Slider {
    SliderControllable target;
    public FrameSlider(SliderControllable target){
        super();
        this.target = target;
        this.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                target.onSliderUpdate(oldValue, newValue);
            }
        });
    }

}
