package com.sikuinnova.canvachallenge.presentation.home;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by josearce on 6/30/17.
 */
interface HomeActivityView {

    void renderImage(Bitmap bitmap);

    void showError(String errorMessage);

    Context getContext();
}
