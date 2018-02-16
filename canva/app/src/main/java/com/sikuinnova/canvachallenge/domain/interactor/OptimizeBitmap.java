package com.sikuinnova.canvachallenge.domain.interactor;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.sikuinnova.canvachallenge.domain.exception.ImageNotFoundException;
import com.sikuinnova.canvachallenge.domain.executor.PostExecutionThread;
import com.sikuinnova.canvachallenge.domain.executor.ThreadExecutor;
import com.sikuinnova.canvachallenge.presentation.tiles.ImageSelected;

import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by josearce on 6/29/17.
 */
public class OptimizeBitmap extends UseCase {

    private ImageSelected imageSelected;

    public OptimizeBitmap(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    public void setImageSelected(ImageSelected imageSelected) {
        this.imageSelected = imageSelected;
    }

    @Override
    protected Observable<Bitmap> buildUseCaseObservable() {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = decodeSampledBitmapFromInputStream(imageSelected);
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(new ImageNotFoundException());
                }
            }
        });
    }

    private Bitmap decodeSampledBitmapFromInputStream(ImageSelected imageSelected) throws IOException {
        ContentResolver contentResolver = imageSelected.getContentResolver();
        Uri uri = imageSelected.getUri();
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream inputStream = contentResolver.openInputStream(uri);
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, imageSelected.getReqWidth(), imageSelected.getReqHeight());

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        InputStream inputStream1 = contentResolver.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream1, null, options);

        inputStream1.close();
        inputStream.close();
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
