package com.trackerapp;

import javafx.event.Event;

public interface SliderControllable {
    void onSliderUpdate(Number oldValue, Number newValue);
}
