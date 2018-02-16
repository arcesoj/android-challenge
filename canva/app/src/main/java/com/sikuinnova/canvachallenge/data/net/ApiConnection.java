package com.sikuinnova.canvachallenge.data.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by josearce on 6/30/17.
 */
class ApiConnection {

    private URL url;
    private Bitmap bitmap;

    private ApiConnection(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    static ApiConnection newInstance(String url) throws MalformedURLException {
        return new ApiConnection(url);
    }

    Bitmap request() {
        connectToApi();
        return bitmap;
    }

    private void connectToApi() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
