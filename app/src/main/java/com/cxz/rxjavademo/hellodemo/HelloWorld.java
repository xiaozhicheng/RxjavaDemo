package com.cxz.rxjavademo.hellodemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.cxz.rxjavademo.R;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * author: Shoxz.Cheng
 * Date: 2017-01-03
 * Time: 10:47
 */
public class HelloWorld extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        helloworld();

    }

    /**
     * 基础
     */
    public void helloworld(){
        //创建一个Observable对象很简单，直接调用Observable.create即可
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello World");
                subscriber.onCompleted();  //必须
            }
        });

        //接着我们创建一个Subscriber来处理Observable对象发出的字符串。
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(String s) {
                Toast.makeText(HelloWorld.this, s, Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * 这里subscriber仅仅就是打印observable发出的字符串。
         * 通过subscribe函数就可以将我们定义的Observable对象和Subscriber对象关联起来，
         * 这样就完成了subscriber对observable的订阅。
         */

        observable.subscribe(subscriber);

        //一旦mySubscriber订阅了myObservable，observable就是调用subscriber对象的onNext和onComplete方法，
        // mySubscriber就会打印出Hello World！
    }

    /**
     * 简化
     */
    public void simpler(){

        /**
         * RxJava内置了很多简化创建Observable对象的函数，
         * 比如 Observable.just就是用来创建只发出一个事件就结束的Observable对象，
         * 上面创建Observable对象的代码可以简化为一行
         */

        Observable<String> observable = Observable.just("Hello World");

        /**
         * 接下来看看如何简化Subscriber，上面的例子中，我们其实并不关心OnComplete和OnError，
         * 我们只需要在onNext的时候做一些处理，这时候就可以使用Action1类。
         */
        Action1<String> action1 = new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(HelloWorld.this, s, Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * subscribe方法有一个重载版本，接受三个Action1类型的参数，
         * 分别对应OnNext，OnComplete， OnError函数。
         * observable.subscribe(onNextAction, onErrorAction, onCompleteAction);
         * 这里我们并不关心onError和onComplete
         */

        observable.subscribe(action1);

        //上面的代码最终可以写成这样
        Observable.just("Hello World").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(HelloWorld.this, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 变换,操作符
     */
    public void operator1(){
        Observable.just("Hello World").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s+"shoxz";
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(HelloWorld.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        // output "Hello World shoxz"

        //Observable.from()方法，它接收一个集合作为输入，然后每次输出一个元素给subscriber：
        String[] strings = new String[]{"1","2","3"};
        Observable.from(strings).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.print(s);
            }
        });

        // output "1"  /n  "2"  /n  "3"

        /**Observable.flatMap()接收一个Observable的输出作为输入，同时输出另外一个Observable。直接看代码：
         *flatMap()是不是看起来很奇怪？为什么它要返回另外一个Observable呢？
         *理解flatMap的关键点在于，flatMap输出的新的 Observable正是我们在Subscriber想要接收的。
         * 现在Subscriber不再收到List<String>，而是收到一些 列单个的字符串，就像Observable.from()的输出一样。
         */
        final List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Observable.create(new Observable.OnSubscribe<List<String>>(){
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
        .flatMap(new Func1<List<String>, Observable<String>>() {
            @Override
            public Observable<String> call(List<String> strings) {
                return Observable.from(strings);
            }
        })
        .flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return Observable.just("Hello-->"+s);
            }
        })
        //filter()输出和输入相同的元素，并且会过滤掉那些不满足检查条件的。
        .filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return !TextUtils.isEmpty(s);
            }
        })
        //如果我们只想要最多5个结果：
        .take(5)
        //doOnNext()允许我们在每次输出一个元素之前做一些额外的事情，比如这里的保存字符串。
        .doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                //保存动作
            }
        })
        //只接收string动作，并不关心做了什么操作符操作
        .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.print(s);
            }
        });



    }

    public void operator2(){
        /**
         * new Func1<String, String>() string 转为 string
         * new Func1<String, Integer>()  string 转为 integer
         * new Func1<String, T>() string 转为任何 对象
         */

        Observable.just("Hello world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Toast.makeText(HelloWorld.this, String.valueOf(integer), Toast.LENGTH_SHORT).show();
                    }
        });
    }

    public void operator3(){

        /**
         * Subscriber做的事情越少越好，我们再增加一个map操作符
         */
        Observable.just("Hello world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return String.valueOf(integer);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(HelloWorld.this, s, Toast.LENGTH_SHORT).show();
                    }
        });
    }

    /**
     * Observable可以是一个数据库查询，Subscriber用来显示查询结果；
     * Observable可以是屏幕上的点击事件，Subscriber用来响应点击事件；
     * Observable可以是一个网络请求，Subscriber用来显示请求结果。
     */
    public void usedemo(){
        //3秒后执行
        Observable.timer(3, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Toast.makeText(HelloWorld.this, "Hello World", Toast.LENGTH_SHORT).show();
            }
        });

        //每3秒执行一次
        Observable.interval(3, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Toast.makeText(HelloWorld.this, "Hello World", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void rxbinding(){

        //防止按钮点击两次
        RxView.clicks(findViewById(R.id.button))
                .throttleFirst(1,TimeUnit.SECONDS)
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        Toast.makeText(HelloWorld.this, "Hello World", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 减少网络请求：
     * 之前：没输入一个字符请求一次
     * 以上存在两个问题：
     *每输入一个字母（对的这很坑）比如：用户快速输入了一个“a”，然后“ab”然后“abc”然后又纠正为“ab”并最终想搜索“abe”。
     * 这样你就做了5次网络请求。想象一在网速很慢的时候是个什么情况。
     *
     *你还面临一个线程赛跑的问题。比如：用户输入了“a”，然后是“ab”。“ab”的网络调用发生在前而”a“的调用发生在后。
     * 那样的话updateList() 将根据 “a”的请求结果来执行。
     *
     * 解决：你需要的是 debounce()  。根据我的经验，取值在100–150毫秒效果最好。
     * 如果你的服务器需要额外的300毫秒那么你可以在0.5秒之内做UI更新。
     */
    public void repeatRequest(){
        EditText editText = new EditText(this);
        RxTextView.textChanges(editText)
                .debounce(150,TimeUnit.MILLISECONDS)
                .flatMap(new Func1<CharSequence, Observable<?>>() {
                    @Override
                    public Observable<?> call(CharSequence charSequence) {
                        return null;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                    }
                });

        /**
         * 引入  switchMap 来替代flatMap。它会停止前面发出的items。
         * 所以如果在0+150ms时你搜索“ab”，在 0+300ms时搜索“abcd”，但是“ab”的网络调用需要 150ms以上的时间才能完成，
         * 那么到了开始“abcd”调用的时候前面的那个会被取消。这样你总是能得到最近的请求数据。
         */

        RxTextView.textChanges(editText)
                .debounce(150,TimeUnit.MILLISECONDS)
                .switchMap(new Func1<CharSequence, Observable<?>>() {
                    @Override
                    public Observable<?> call(CharSequence charSequence) {
                        return null;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                    }
                });

        //如果所有的网络调用都失败，那么你将不能再次观察到text的改变。
        //它将等待5秒让手机网络畅通，如果超过则会抛出一个异常。如果用户重试它则会等待更长的超时时间（比如15秒）。
        RxTextView.textChanges(editText)
                .debounce(150,TimeUnit.MILLISECONDS)
                .switchMap(new Func1<CharSequence, Observable<?>>() {
                    @Override
                    public Observable<?> call(CharSequence charSequence) {
                        return null;
                    }
                })
                .retryWhen(new RetryWithConnectivityIncremental(this,5,15,TimeUnit.SECONDS))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                    }
                });

    }

}
