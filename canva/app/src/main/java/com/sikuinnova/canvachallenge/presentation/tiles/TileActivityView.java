package com.sikuinnova.canvachallenge.presentation.tiles;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by josearce on 6/30/17.
 */
interface TileActivityView {

    void renderBitmap(Bitmap bitmap);

    void result(String result);

    void optimizeBitmap(Bitmap bitmap);

    void showLoading();

    void hideLoading();

    Context getContext();
}
