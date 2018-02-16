package com.sikuinnova.canvachallenge.presentation.home;

import android.graphics.Bitmap;

import com.sikuinnova.canvachallenge.domain.exception.DefaultErrorBundle;
import com.sikuinnova.canvachallenge.domain.exception.ErrorBundle;
import com.sikuinnova.canvachallenge.domain.interactor.DefaultSubscriber;
import com.sikuinnova.canvachallenge.domain.interactor.OptimizeBitmap;
import com.sikuinnova.canvachallenge.presentation.exception.ErrorMessageFactory;
import com.sikuinnova.canvachallenge.presentation.presenter.BasePresenter;
import com.sikuinnova.canvachallenge.presentation.tiles.ImageSelected;

/**
 * Created by josearce on 6/30/17.
 */
class HomeActivityPresenter implements BasePresenter {

    private OptimizeBitmap optimizeBitmap;
    private HomeActivityView homeActivityView;

    HomeActivityPresenter(OptimizeBitmap optimizeBitmap) {
        this.optimizeBitmap = optimizeBitmap;
    }

    void setView(HomeActivityView homeActivityView) {
        this.homeActivityView = homeActivityView;
    }

    void optimizeBitmap(ImageSelected imageSelected) {
        optimizeBitmap.setImageSelected(imageSelected);
        optimizeBitmap.execute(new DefaultSubscriber<Bitmap>() {
            @Override
            public void onNext(Bitmap bitmap) {
                super.onNext(bitmap);
                homeActivityView.renderImage(bitmap);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            }
        });
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(homeActivityView.getContext(), errorBundle.getException());
        homeActivityView.showError(errorMessage);
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
        homeActivityView = null;
    }
}
