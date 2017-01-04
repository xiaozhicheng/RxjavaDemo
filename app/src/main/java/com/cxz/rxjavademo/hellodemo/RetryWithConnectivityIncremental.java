package com.cxz.rxjavademo.hellodemo;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Shoxz.Cheng
 * 2017/1/3 15:49
 */

public class RetryWithConnectivityIncremental implements Func1<Observable<? extends Throwable>,Observable<?>> {

    private final int maxTimeout;
    private final TimeUnit timeUnit;
    private final Observable<Boolean> isConnected;
    private final int startTimeout;
    private int timeout;

    public RetryWithConnectivityIncremental(Context context,int startTimeout,int maxTimeout,TimeUnit timeUnit) {
        this.maxTimeout = maxTimeout;
        this.timeUnit = timeUnit;
        this.startTimeout = startTimeout;
        isConnected = getConnectedObservable(context);
    }


    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable throwable) {

//                if(throwable instanceof Retr)


                return null;
            }
        });
    }

    private Observable<Boolean> getConnectedObservable(Context context) {
        return BroadcastObservable.fromConnectivityManager(context)
                .distinctUntilChanged()
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                });
    }
}
