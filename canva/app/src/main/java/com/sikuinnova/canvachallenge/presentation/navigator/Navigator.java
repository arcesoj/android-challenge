package com.sikuinnova.canvachallenge.presentation.navigator;

import android.content.Context;
import android.content.Intent;

import com.sikuinnova.canvachallenge.presentation.tiles.TileActivity;

/**
 * Created by josearce on 6/30/17.
 */
public class Navigator {

    private Navigator(){

    }

    public static void navigateToTileActivity(int numberTiles, String urlImage, Context context) {
        Intent intent = TileActivity.callingIntent(numberTiles, urlImage, context);
        context.startActivity(intent);
    }

}
