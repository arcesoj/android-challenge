package com.sikuinnova.canvachallenge.data.net;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.BuildConfig;
import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import rx.Observable;

/**
 * Created by josearce on 6/30/17.
 */
public interface RestApi {

    String API_URL_GET_TILE = BuildConfig.API + "color/";

    Observable<Bitmap> getTile(TileModel tileModel);

}
