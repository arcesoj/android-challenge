package com.sikuinnova.canvachallenge.domain.executor;

import rx.Scheduler;

/**
 * Created by josearce on 6/27/17.
 */
public interface PostExecutionThread {
    Scheduler getScheduler();
}
