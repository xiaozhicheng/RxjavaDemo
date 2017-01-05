package com.cxz.rxjavademo.rxbusdemo;

import android.os.Bundle;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * author: Shoxz.Cheng
 * Date: 2017-01-05
 * Time: 10:38
 */
public class RxBus {

    private final Subject<Object,Object> rxbus = new SerializedSubject<>(PublishSubject.create());

    private RxBus(){}

    public static RxBus getIntance(){
        return RxbusHolder.instance;
    }

    public void send(Bundle bundle){
        rxbus.onNext(bundle);
    }

    public Observable<Object> toObserverable(){
        return rxbus;
    }


    public static class RxbusHolder{
        private static final RxBus instance = new RxBus();
    }
}
