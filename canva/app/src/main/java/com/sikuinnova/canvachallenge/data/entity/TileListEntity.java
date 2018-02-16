package com.sikuinnova.canvachallenge.data.entity;

import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import java.util.List;

/**
 * Created by josearce on 6/30/17.
 */
public class TileListEntity {

    private final List<TileModel> tileModels;

    public TileListEntity(List<TileModel> tileModels) {
        this.tileModels = tileModels;
    }

    public List<TileModel> getTileModels() {
        return tileModels;
    }
}
