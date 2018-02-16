package com.sikuinnova.canvachallenge.presentation.tiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sikuinnova.canvachallenge.R;
import com.sikuinnova.canvachallenge.data.executor.JobExecutor;
import com.sikuinnova.canvachallenge.data.net.RestApi;
import com.sikuinnova.canvachallenge.data.net.RestApiImpl;
import com.sikuinnova.canvachallenge.data.repository.TileDataRepository;
import com.sikuinnova.canvachallenge.data.repository.datasource.CloudTileDataStore;
import com.sikuinnova.canvachallenge.data.repository.datasource.TileDataStore;
import com.sikuinnova.canvachallenge.domain.interactor.GetTiles;
import com.sikuinnova.canvachallenge.domain.interactor.OptimizeBitmap;
import com.sikuinnova.canvachallenge.domain.repository.TileRepository;
import com.sikuinnova.canvachallenge.presentation.UIThread;

public class TileActivity extends AppCompatActivity implements TileActivityView {

    private static final String NUMBER_TILES_PARAM = "number_tiles";
    private static final String URL_IMAGE_PARAM = "url_image";
    private TextView resultTextView;
    private ImageView imageView;
    private ImageView imageViewResult;
    private ProgressBar progressBar;
    private TileActivityPresenter presenter;

    public static Intent callingIntent(int numberTiles, String urlImage, Context context) {
        Intent intent = new Intent(context, TileActivity.class);
        intent.putExtra(NUMBER_TILES_PARAM, numberTiles);
        intent.putExtra(URL_IMAGE_PARAM, urlImage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultTextView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setDimension();
    }

    private void setDimension() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int heightImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, displayMetrics);
        int widthImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, displayMetrics);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthImage, heightImage);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;

        imageView.setLayoutParams(params);
        imageViewResult.setLayoutParams(params);

        Uri selectedImage = Uri.parse(getIntent().getStringExtra(URL_IMAGE_PARAM));
        ImageSelected imageSelected = new ImageSelected(getContentResolver(), selectedImage, widthImage, heightImage);
        setupPresenter(imageSelected);
    }

    private void setupPresenter(ImageSelected imageSelected) {
        JobExecutor jobExecutor = new JobExecutor();
        UIThread uiThread = new UIThread();
        OptimizeBitmap optimizeBitmap = new OptimizeBitmap(jobExecutor, uiThread);
        RestApi restApi = new RestApiImpl(getApplicationContext());
        TileDataStore tileDataStore = new CloudTileDataStore(restApi);
        TileRepository tileRepository = new TileDataRepository(tileDataStore);
        final int number_tiles_default = 32;

        int numberTiles = getIntent().getIntExtra(NUMBER_TILES_PARAM, number_tiles_default);
        GetTiles getTiles = new GetTiles(numberTiles, false, tileRepository, jobExecutor, uiThread);

        presenter = new TileActivityPresenter(getTiles, optimizeBitmap);
        presenter.setView(this);
        presenter.optimizeBitmap(imageSelected);
    }

    @Override
    public void renderBitmap(Bitmap bitmap) {
        imageViewResult.setImageBitmap(bitmap);
    }

    @Override
    public void optimizeBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        presenter.renderBitmap(bitmap);
    }

    @Override
    public void result(String result) {
        resultTextView.setText(result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId() == android.R.id.home) {
           onBackPressed();
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
