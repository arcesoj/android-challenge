package com.sikuinnova.canvachallenge.domain.interactor.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by josearce on 7/7/17.
 */
public class TileBitmap {

    private final Bitmap bitmap;
    private final int chunkSideLength;
    private final Canvas canvas;
    private int positionY;

    public TileBitmap(int width, int height, int chunkSideLength) {
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.canvas = new Canvas(bitmap);
        this.chunkSideLength = chunkSideLength;
    }

    public Bitmap createBitmap(List<Bitmap> tileModels) {
        int xCoord = 0;
        for (Bitmap currentChunk : tileModels) {
            canvas.drawBitmap(currentChunk, xCoord, positionY * chunkSideLength, null);
            xCoord += chunkSideLength;
        }

        positionY++;
        return bitmap;
    }
}
