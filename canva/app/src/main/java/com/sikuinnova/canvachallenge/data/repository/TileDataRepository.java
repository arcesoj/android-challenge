package com.sikuinnova.canvachallenge.data.repository;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.data.entity.TileListEntity;
import com.sikuinnova.canvachallenge.data.repository.datasource.TileDataStore;
import com.sikuinnova.canvachallenge.domain.repository.TileRepository;
import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by josearce on 6/30/17.
 */
public class TileDataRepository implements TileRepository {

    private TileDataStore tileDataStore;

    public TileDataRepository(TileDataStore tileDataStore) {
        this.tileDataStore = tileDataStore;
    }

    @Override
    public Observable<List<Bitmap>> getTile(final TileListEntity tileEntityList) {
        return Observable.from(tileEntityList.getTileModels())
                .flatMap(new Func1<TileModel, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> call(TileModel tileEntity) {
                        return tileDataStore.tileEntity(tileEntity);
                    }
                }).toList().flatMap(new Func1<List<Bitmap>, Observable<List<Bitmap>>>() {
                    @Override
                    public Observable<List<Bitmap>> call(final List<Bitmap> bitmaps) {
                        return Observable.create(new Observable.OnSubscribe<List<Bitmap>>() {
                            @Override
                            public void call(Subscriber<? super List<Bitmap>> subscriber) {
                                subscriber.onNext(bitmaps);
                                subscriber.onCompleted();
                            }
                        });
                    }
                });
    }

}
