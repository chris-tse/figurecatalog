package com.chris.tse;

/**
 * Created by christophertse on 2017/03/25.
 */
public enum FigureSize {
    SMALL ("Small"),
    MEDIUM ("Medium"),
    LARGE ("Large");

    private final String size;

    FigureSize(String size) {
        this.size = size;
    }
}
