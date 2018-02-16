package com.sikuinnova.canvachallenge.presentation;

import com.sikuinnova.canvachallenge.domain.executor.PostExecutionThread;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by josearce on 6/27/17.
 */
public class UIThread implements PostExecutionThread {

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }

}