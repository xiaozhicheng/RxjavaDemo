package com.cxz.rxjavademo.hellodemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * author: Shoxz.Cheng
 * Date: 2017-01-03
 * Time: 15:34
 */
public class BroadcastObservable implements Observable.OnSubscribe<Boolean> {

    private final Context context;


    public static Observable<Boolean> fromConnectivityManager(Context context){
        return Observable.create(new BroadcastObservable(context)).share();
    }


    public BroadcastObservable(Context context) {
        this.context = context;
    }

    @Override
    public void call(final Subscriber<? super Boolean> subscriber) {

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                subscriber.onNext(isConnectedInternet());
            }
        };

        context.registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        subscriber.add(unSunscribleInUiThread(new Action0() {
            @Override
            public void call() {
                context.unregisterReceiver(receiver);
            }
        }));
    }


    private boolean isConnectedInternet(){
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    private static Subscription unSunscribleInUiThread(final Action0 unsubscrible){
        return Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if(Looper.getMainLooper() == Looper.myLooper()){
                    unsubscrible.call();
                }else {
                    final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                    inner.schedule(new Action0() {
                        @Override
                        public void call() {
                            unsubscrible.call();
                            inner.unsubscribe();
                        }
                    });
                }
            }
        });
    }
}
