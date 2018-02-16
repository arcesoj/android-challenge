package com.sikuinnova.canvachallenge.presentation.tiles;

/**
 * Created by josearce on 6/27/17.
 */
public class TileModel {

    private final int width;
    private final int height;
    private final String averageColor;

    public TileModel(int width, int height, String averageColor) {
        this.width = width;
        this.height = height;
        this.averageColor = averageColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getAverageColor() {
        return averageColor;
    }

    @Override
    public String toString() {
        return "TileModel :  Average color :" + averageColor;
    }

}
