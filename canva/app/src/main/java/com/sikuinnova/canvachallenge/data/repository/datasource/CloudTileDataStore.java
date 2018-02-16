package com.sikuinnova.canvachallenge.data.repository.datasource;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.data.net.RestApi;
import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by josearce on 6/30/17.
 */
public class CloudTileDataStore implements TileDataStore {

    private RestApi restApi;

    public CloudTileDataStore(RestApi restApi) {
        this.restApi = restApi;
    }

    @Override
    public Observable<Bitmap> tileEntity(final TileModel tileEntity) {
        return restApi.getTile(tileEntity).flatMap(new Func1<Bitmap, Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call(final Bitmap bitmap) {
                return Observable.create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

}
