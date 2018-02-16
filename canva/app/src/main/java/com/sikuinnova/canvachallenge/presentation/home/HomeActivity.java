package com.sikuinnova.canvachallenge.presentation.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sikuinnova.canvachallenge.R;
import com.sikuinnova.canvachallenge.data.executor.JobExecutor;
import com.sikuinnova.canvachallenge.domain.interactor.OptimizeBitmap;
import com.sikuinnova.canvachallenge.presentation.UIThread;
import com.sikuinnova.canvachallenge.presentation.navigator.Navigator;
import com.sikuinnova.canvachallenge.presentation.tiles.ImageSelected;

public class HomeActivity extends AppCompatActivity implements HomeActivityView {

    private static final int REQUEST_PICK_IMAGE = 100;
    private EditText numberTileEditText;
    private View.OnClickListener selectImageFromGallery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Create intent to Gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
        }
    };
    private Button selectImageButton;
    private ImageView imageView;
    private HomeActivityPresenter presenter;
    private String uriImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        numberTileEditText = (EditText) findViewById(R.id.home_number_tiles_edit_text);
        selectImageButton = (Button) findViewById(R.id.home_button_view);
        imageView = (ImageView) findViewById(R.id.home_image_view);
        selectImageButton.setOnClickListener(selectImageFromGallery);
        numberTileEditText.setSelection(numberTileEditText.getText().toString().length());

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int heightImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, displayMetrics);
        int widthImage = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, displayMetrics);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthImage, heightImage);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        imageView.setLayoutParams(params);

        OptimizeBitmap optimizeBitmap = new OptimizeBitmap(new JobExecutor(), new UIThread());
        presenter = new HomeActivityPresenter(optimizeBitmap);
        presenter.setView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            uriImage = selectedImage.toString();
            ImageSelected imageSelected = new ImageSelected(getContentResolver(), selectedImage, imageView.getWidth(), imageView.getHeight());
            presenter.optimizeBitmap(imageSelected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public void renderImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectImageButton = null;
        presenter.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home_next)
                navigatorToTileActivity();

        return super.onOptionsItemSelected(item);
    }

    private void navigatorToTileActivity() {
        String numberTiles = numberTileEditText.getText().toString();

        if (numberTiles.isEmpty()) {
            showError(getString(R.string.home_error_number_tiles));
            return;
        }
        if (uriImage.isEmpty()) {
            showError(getString(R.string.home_error_uri_image));
            return;
        }

        Navigator.navigateToTileActivity(Integer.parseInt(numberTiles), uriImage, HomeActivity.this);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
