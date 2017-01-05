package com.cxz.rxjavademo.usedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.cxz.rxjavademo.R;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author: Shoxz.Cheng
 * Date: 2017-01-04
 * Time: 16:53
 */
public class ImageActivity extends Activity{


    int resid = R.mipmap.ic_launcher;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.image);
        setImage();
    }



    public void setImage(){


        Observable.create(new Observable.OnSubscribe<Drawable>(){
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getTheme().getDrawable(resid);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())   // 指定 subscribe() 发生在 IO 线程

                .observeOn(AndroidSchedulers.mainThread())   // 指定 Subscriber 的回调发生在主线程

                .subscribe(new Subscriber<Drawable>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }
        });
    }

    public void change(){

        Observable.just(resid)
                .map(new Func1<Integer, Bitmap>() {
                    @Override
                    public Bitmap call(Integer integer) {
                        return getBitmapFromPath(integer);
                    }
                })
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        showBitmap(bitmap);
                    }
                });

    }



    public void showBitmap(Bitmap bm){
        imageView.setImageBitmap(bm);
    }


    public Bitmap getBitmapFromPath(int resid){
        Bitmap bm = BitmapFactory.decodeResource(getResources(),resid);
        return bm;
    }

}
