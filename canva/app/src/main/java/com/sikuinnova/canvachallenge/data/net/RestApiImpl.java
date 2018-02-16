package com.sikuinnova.canvachallenge.data.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.common.base.Preconditions;
import com.sikuinnova.canvachallenge.data.exception.NetworkConnectionException;
import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import java.net.MalformedURLException;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by josearce on 6/30/17.
 */
public class RestApiImpl implements RestApi {

    private Context context;

    public RestApiImpl(Context context) {
        Preconditions.checkNotNull(context);
        this.context = context;
    }

    @Override
    public Observable<Bitmap> getTile(final TileModel tileModel) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if (isThereInternetConnection()) {
                    try {
                        Bitmap bitmap = getTileFromApi(tileModel.getWidth(), tileModel.getHeight(), tileModel.getAverageColor());
                        if (bitmap != null) {
                            subscriber.onNext(bitmap);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new NetworkConnectionException());
                        }
                    } catch (Exception e) {
                        subscriber.onError(new NetworkConnectionException(e.getCause()));
                    }
                } else {
                    subscriber.onError(new NetworkConnectionException());
                }
            }
        });
    }

    private Bitmap getTileFromApi(int width, int height, String color) throws MalformedURLException {
        String url = API_URL_GET_TILE + width + "/" + height + "/" + color;
        return ApiConnection.newInstance(url).request();
    }

    private boolean isThereInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }

}
