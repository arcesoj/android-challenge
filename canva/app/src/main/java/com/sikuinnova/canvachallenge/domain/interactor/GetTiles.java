package com.sikuinnova.canvachallenge.domain.interactor;

import android.graphics.Bitmap;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.common.base.Preconditions;
import com.sikuinnova.canvachallenge.data.entity.TileListEntity;
import com.sikuinnova.canvachallenge.domain.executor.PostExecutionThread;
import com.sikuinnova.canvachallenge.domain.executor.ThreadExecutor;
import com.sikuinnova.canvachallenge.domain.interactor.helper.BitmapHelper;
import com.sikuinnova.canvachallenge.domain.interactor.helper.TileBitmap;
import com.sikuinnova.canvachallenge.domain.repository.TileRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by josearce on 6/27/17.
 */
public class GetTiles extends UseCase {

    private int chunkSideLength;
    private TileRepository tileRepository;
    private BitmapHelper bitmapHelper;
    private TileBitmap tileBitmap;
    private boolean dynamicTile;

    public GetTiles(int chunkSideLength, boolean dynamic, TileRepository tileRepository, ThreadExecutor threadExecutor,
                    PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.dynamicTile = dynamic;
        this.chunkSideLength = chunkSideLength;
        this.tileRepository = tileRepository;
    }

    public void setBitmap(Bitmap bitmap) {
        Preconditions.checkNotNull(bitmap);
        this.tileBitmap = new TileBitmap(bitmap.getWidth(), bitmap.getHeight(), chunkSideLength);
        this.bitmapHelper = new BitmapHelper(chunkSideLength, bitmap);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return Observable.create(new Observable.OnSubscribe<List<TileListEntity>>() {
            @Override
            public void call(Subscriber<? super List<TileListEntity>> subscriber) {
                //Split bitmap in tiles
                subscriber.onNext(bitmapHelper.splitBitmapInTilesRow(dynamicTile));
                subscriber.onCompleted();
            }
        }).flatMap(new Func1<List<TileListEntity>, Observable<List<Bitmap>>>() {
            @Override
            public Observable<List<Bitmap>> call(List<TileListEntity> tileList) {
                // Execute each row
                return createObservableByRow(tileList, tileRepository);
            }
        }).flatMap(new Func1<List<Bitmap>, Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call(final List<Bitmap> tileModels) {
                // Emmit Bitmap to TileActivityPresenter
                return Observable.create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        subscriber.onNext(tileBitmap.createBitmap(tileModels));
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    @RxLogObservable
    private Observable<List<Bitmap>> createObservableByRow(List<TileListEntity> tileRowList, TileRepository tileRepository) {
        List<Observable<List<Bitmap>>> observables = new ArrayList<>();
        for (TileListEntity tileListEntity : tileRowList) {
            observables.add(tileRepository.getTile(tileListEntity).subscribeOn(Schedulers.io()));
        }
        return Observable.concatEager(observables);
    }

}
