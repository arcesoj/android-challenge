package com.sikuinnova.canvachallenge.presentation.tiles;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.common.base.Preconditions;

/**
 * Created by josearce on 6/29/17.
 */
public class ImageSelected {

    private final ContentResolver contentResolver;
    private final Uri uri;
    private final int reqWidth;
    private final int reqHeight;

    public ImageSelected(ContentResolver contentResolver, Uri uri, int reqWidth, int reqHeight) {
        Preconditions.checkNotNull(contentResolver);
        Preconditions.checkNotNull(uri);
        this.contentResolver = contentResolver;
        this.uri = uri;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public int getReqWidth() {
        return reqWidth;
    }

    public int getReqHeight() {
        return reqHeight;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public Uri getUri() {
        return uri;
    }
}
