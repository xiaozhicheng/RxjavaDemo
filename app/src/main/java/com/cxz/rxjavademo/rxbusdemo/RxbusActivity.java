package com.cxz.rxjavademo.rxbusdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cxz.rxjavademo.R;

import rx.subscriptions.CompositeSubscription;

/**
 * author: Shoxz.Cheng
 * Date: 2017-01-05
 * Time: 10:54
 */
public class RxbusActivity extends Activity {

    private Button button;
    private CompositeSubscription allSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bus","event");
                RxBus.getIntance().send(bundle);
            }
        });

        allSubscription.add(RxBus.getIntance().toObserverable().subscribe());

    }
}
