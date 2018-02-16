package com.sikuinnova.canvachallenge.presentation.tiles;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.domain.exception.DefaultErrorBundle;
import com.sikuinnova.canvachallenge.domain.exception.ErrorBundle;
import com.sikuinnova.canvachallenge.domain.interactor.DefaultSubscriber;
import com.sikuinnova.canvachallenge.domain.interactor.GetTiles;
import com.sikuinnova.canvachallenge.domain.interactor.OptimizeBitmap;
import com.sikuinnova.canvachallenge.presentation.exception.ErrorMessageFactory;
import com.sikuinnova.canvachallenge.presentation.presenter.BasePresenter;

/**
 * Created by josearce on 6/27/17.
 */
class TileActivityPresenter implements BasePresenter {

    private TileActivityView view;
    private GetTiles getTiles;
    private OptimizeBitmap optimizeBitmap;

    TileActivityPresenter(GetTiles getTiles, OptimizeBitmap optimizeBitmap) {
        this.getTiles = getTiles;
        this.optimizeBitmap = optimizeBitmap;
    }

    void setView(TileActivityView view) {
        this.view = view;
    }

    void renderBitmap(Bitmap bitmap) {
        view.result("In Progress....");
        view.showLoading();
        getTiles.setBitmap(bitmap);
        getTiles.execute(new RenderBitmapSubscriber());
    }

    void optimizeBitmap(ImageSelected imageSelected) {
        optimizeBitmap.setImageSelected(imageSelected);
        optimizeBitmap.execute(new OptimizeBitmapSubscriber());
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(view.getContext(), errorBundle.getException());
        view.result(errorMessage);
    }

    @Override
    public void onResume() {
        // No necessary at the moment
    }

    @Override
    public void onStart() {
        // No necessary at the moment
    }

    @Override
    public void onDestroy() {
        optimizeBitmap.unsubscribe();
        getTiles.unsubscribe();
        view = null;
    }

    private class RenderBitmapSubscriber extends DefaultSubscriber<Bitmap> {
        @Override
        public void onNext(Bitmap tileList) {
            super.onNext(tileList);
            view.renderBitmap(tileList);
            view.result("It was successful");
            view.hideLoading();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            view.hideLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
        }
    }

    private class OptimizeBitmapSubscriber extends DefaultSubscriber<Bitmap> {
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            showErrorMessage(new DefaultErrorBundle((Exception) e));
        }

        @Override
        public void onNext(Bitmap bitmap) {
            super.onNext(bitmap);
            view.optimizeBitmap(bitmap);
        }
    }

}
