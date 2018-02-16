package com.sikuinnova.canvachallenge.domain.repository;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.data.entity.TileListEntity;

import java.util.List;

import rx.Observable;

/**
 * Created by josearce on 6/30/17.
 */
public interface TileRepository {

    Observable<List<Bitmap>> getTile(TileListEntity list);

}
