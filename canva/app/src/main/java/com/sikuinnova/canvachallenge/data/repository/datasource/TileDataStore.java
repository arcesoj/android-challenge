package com.sikuinnova.canvachallenge.data.repository.datasource;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import rx.Observable;

/**
 * Created by josearce on 6/30/17.
 */
public interface TileDataStore {

    Observable<Bitmap> tileEntity(TileModel tileEntity);

}
