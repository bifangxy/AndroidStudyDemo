package com.xy.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xy.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.observable.ObservableSubscribeOn;
import io.reactivex.observables.GroupedObservable;

public class MainActivity extends AppCompatActivity {


    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisposable = Observable.range(10, 20).subscribe(i -> System.out.println("i = " + i));

        mDisposable = Observable.create((ObservableOnSubscribe<Integer>) observableEmitter -> {
            observableEmitter.onNext(1);
            observableEmitter.onNext(2);
            observableEmitter.onNext(3);
        }).subscribe(System.out::println);


        Observable<Long> observable = Observable.defer((Callable<ObservableSource<Long>>) () -> Observable.just(System.currentTimeMillis()));
        mDisposable = observable.subscribe(System.out::print);

        System.out.println("----");
        mDisposable = observable.subscribe(System.out::print);


        mDisposable = Observable.empty().subscribe(i -> System.out.println("next"), i -> System.out.println("error"), () -> System.out.println("complete"));
        mDisposable = Observable.never().subscribe(i -> System.out.println("next"), i -> System.out.println("error"), () -> System.out.println("complete"));
        mDisposable = Observable.error(new Exception()).subscribe(i -> System.out.println("next"), i -> System.out.println("error"), () -> System.out.println("complete"));


        String[] arrays = new String[]{"Rxjava", "Android", "Java"};


        mDisposable = Observable.fromArray(arrays).subscribe(i -> System.out.println(i));

        Observable observable1 = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                Thread.sleep(1000);
                return "Rxjava";
            }
        });

        mDisposable = observable1.subscribe(System.out::println);


        FutureTask<String> futureTask = new FutureTask<>(() -> "返回结果");
        mDisposable = Observable.fromFuture(futureTask)
                .doOnSubscribe(disposable -> futureTask.run())
                .subscribe(s -> LogUtils.i(s));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Rxjava");
        arrayList.add("Android");
        arrayList.add("Java");
        mDisposable = Observable.fromIterable(arrayList)
                .subscribe(s -> LogUtils.i(s));


        mDisposable = Observable.just(1, 2, 3)
                .subscribe(i -> LogUtils.d("i = " + i));

        mDisposable = Observable.range(10, 11).repeat(10)
                .subscribe(i -> LogUtils.d("i = " + i));

        mDisposable = Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(i -> LogUtils.i("i = " + i));

//
//        Observable.intervalRange(10, 20, 1000, 1000, TimeUnit.MILLISECONDS)
//                .map(aLong -> "I am " + String.valueOf(aLong))
//                .subscribe(s -> LogUtils.d("s = " + s));

//        Observable.intervalRange(10, 20, 1000, 1000, TimeUnit.MILLISECONDS)
//                .cast(String.class)
//                .subscribe(s -> LogUtils.i(s));

        List<String> actionList = new ArrayList<>();
        actionList.add("读书");
        actionList.add("打球");
        actionList.add("睡觉");

        List<String> actionList2 = new ArrayList<>();
        actionList.add("打游戏");
        actionList.add("看电视");
        actionList.add("睡觉");

        Plan plan = new Plan();
        plan.setName("读书");
        plan.setActionList(actionList);

        Plan plan2 = new Plan();
        plan2.setName("玩游戏");
        plan2.setActionList(actionList2);

        Person person = new Person();
        person.setName("小明");
        List<Plan> planList = new ArrayList<>();
        planList.add(plan);
        planList.add(plan2);
        person.setPlanList(planList);

        List<Person> personList = new ArrayList<>();
        personList.add(person);
        personList.add(person);

        mDisposable = Observable.fromIterable(personList)
                .flatMap((Function<Person, ObservableSource<Plan>>) person1 -> Observable.fromIterable(person1.getPlanList()))
                .flatMap((Function<Plan, ObservableSource<String>>) plan1 -> Observable.fromIterable(plan1.getActionList()))
                .subscribe(s -> LogUtils.d(s));

//        Observable.intervalRange(1,100,100,100,TimeUnit.MILLISECONDS)
//                .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
//                    if(aLong%10 == 0)
//                        return Observable.just("You are"+aLong).delay(500,TimeUnit.MILLISECONDS);
//                    return Observable.just("You are"+aLong);
//                })
//                .subscribe(s -> LogUtils.i(s));

//        Observable.intervalRange(1, 10, 100, 100, TimeUnit.MILLISECONDS)
//                .concatMap((Function<Long, ObservableSource<String>>) aLong -> {
//                    if (aLong % 10 == 0)
//                        return Observable.just("You are" + aLong).delay(500, TimeUnit.MILLISECONDS);
//                    return Observable.just("You are" + aLong);
//                })
//                .subscribe(s -> LogUtils.i(s));


        // 已连接到 CRANE M2_2862, MAC=A4:CF:12:5E:28:62, 固件版本=1.90, 型号=CRANE_M2, 生产序列号=000000000000000
        mDisposable = Observable.just(1, 2, 3, 4, 5, 6, 7)
                .buffer(2, 3)
                .subscribe(list -> {
                    LogUtils.d("缓存区大小" + list.size());
                    LogUtils.d(list.toString());
                });


        mDisposable = Observable.just(2, 5, 3, 4, 8, 1, 7, 9, 6, 10)
                .groupBy(integer -> integer % 3)
                .subscribe(integerIntegerGroupedObservable -> integerIntegerGroupedObservable.subscribe(integer -> {
                    LogUtils.d("name = " + integerIntegerGroupedObservable.getKey() +
                            "value = " + integer);
                }));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 6)
                .scan((integer, integer2) -> {
                    LogUtils.d("integer = " + integer + "  integer2 = " + integer2);
                    return integer + integer2;
                })
                .subscribe(i -> LogUtils.i("value = " + i));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 6, 7)
                .window(2)
                .subscribe(integerObservable -> {
                    integerObservable.subscribe(integer -> LogUtils.d("window: " + integer),
                            throwable -> LogUtils.d(throwable.toString()),
                            () -> LogUtils.d("----Complete-----"));
                });

        mDisposable = Observable.range(1, 5)
                .flatMapIterable((Function<Integer, Iterable<?>>) integer -> Collections.singletonList("flatMapIterable" + String.valueOf(integer)))
                .subscribe(s -> LogUtils.d("flatMapIterable = " + s));


        mDisposable = Observable.concat(Observable.just(1, 2), Observable.just(3, 4), Observable.just(5, 6),
                Observable.just(7, 8))
                .subscribe(i -> LogUtils.d("concat = " + i));


        mDisposable = Observable.merge(Observable.range(1, 10).map(i -> "A" + i), Observable.range(1, 10).map(i -> "B" + i))
                .subscribe(LogUtils::i);

//        mDisposable = Observable.zip(Observable.intervalRange(1, 5, 1, 1, TimeUnit.SECONDS).map(aLong -> {
//            String s = "A" + aLong;
//            LogUtils.d(s);
//            return s;
//        }), Observable.intervalRange(1, 6, 1, 1, TimeUnit.SECONDS).map(aLong -> {
//            String s = "B" + aLong;
//            LogUtils.d(s);
//            return s;
//        }), (s, s2) -> s + s2)
//                .subscribe(LogUtils::i);

        mDisposable = Observable.combineLatest(Observable.intervalRange(1, 4, 1, 1, TimeUnit.SECONDS).map(aLong -> {
            String s = "A" + aLong;
            LogUtils.d(s);
            return s;
        }), Observable.intervalRange(1, 5, 2, 2, TimeUnit.SECONDS).map(aLong -> {
            String s = "B" + aLong;
            LogUtils.d(s);
            return s;
        }), (s, s2) -> s + s2)
                .subscribe(LogUtils::i, throwable -> LogUtils.d(throwable.toString()), () -> LogUtils.d("---complete----"));

        mDisposable = Observable.just(0, 1, 2, 3, 4, 5, 7)
                .reduce(((integer, integer2) -> {
                    LogUtils.d("integer = " + integer + "   integer2 = " + integer2);
                    int value = integer + integer2;
                    LogUtils.d("value = " + value);
                    return value;
                }))
                .subscribe(value -> LogUtils.i("reduce = " + value));


        mDisposable = Observable.just(1, 2, 3, 4)
                .collect((Callable<ArrayList<Integer>>) () -> new ArrayList(), (integers, integer) -> integers.add(integer))
                .subscribe(s -> LogUtils.i("collect = " + s.toString()));


        mDisposable = Observable.just(4, 5, 6, 7, 8)
                .startWithArray(1, 2, 3)
                .startWith(0)
                .subscribe(value -> LogUtils.i("startWith = " + value));

        mDisposable = Observable.just(4, 5, 6, 7, 8)
                .count()
                .subscribe(value -> LogUtils.i("count = " + value));


        Observable.just(1, 2, 3, 4)
                .delay(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.d("----onSubscribe----");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        LogUtils.d("----onNext----" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d("----onComplete----");

                    }
                });

        mDisposable = Observable.just(1, 2, 3, 4)
                .doOnEach(integerNotification -> LogUtils.d("doOnEach :" + integerNotification.getValue()))
                .subscribe(integer -> LogUtils.d("doOnEach = " + integer));

        mDisposable = Observable.just(1, 2, 3, 4)
                .doOnNext(integer -> LogUtils.d("doOnNext :" + integer))
                .subscribe(integer -> LogUtils.d("doOnNext = " + integer));

        mDisposable = Observable.just(1, 2, 3, 4)
                .doAfterNext(integer -> LogUtils.d("doAfterNext :" + integer))
                .subscribe(integer -> LogUtils.d("doAfterNext = " + integer));

        mDisposable = Observable.just(1, 2, 3, 4)
                .doOnComplete(() -> LogUtils.d("---doOnComplete----"))
                .subscribe(integer -> {
                }, throwable -> {
                }, () -> LogUtils.d("----Complete----"));

        mDisposable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new NullPointerException());
            emitter.onComplete();
        }).doOnError(throwable -> LogUtils.i("doOnError"))
                .subscribe(integer -> {
                }, throwable -> LogUtils.i(throwable.toString()));


        mDisposable = Observable.just(1, 2, 3, 4)
                .doOnSubscribe(disposable -> LogUtils.i("doOnSubscribe"))
                .subscribe(integer -> {
                }, throwable -> {
                }, () -> {
                }, disposable -> LogUtils.i("onSubscribe"));

        mDisposable = Observable.just(1, 2, 3, 4)
                .filter(integer -> integer > 2)
                .subscribe(value -> LogUtils.d("filter" + value));

        mDisposable = Observable.just(1, 2, "3", "4")
                .ofType(String.class)
                .subscribe(value -> LogUtils.d("ofType = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(value -> LogUtils.d("skip = " + value));

//        mDisposable =Observable.intervalRange(1,6,1000,1000,TimeUnit.MILLISECONDS)
//                .skip(2,TimeUnit.SECONDS)
//                .subscribe(value->LogUtils.d("skip time = "+value));

        mDisposable = Observable.just(1, 2, 3, 4, 5)
                .skipLast(2)
                .subscribe(value -> LogUtils.d("skipLast = " + value));
        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .distinct()
                .subscribe(value -> LogUtils.d("distinct = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .distinctUntilChanged()
                .subscribe(value -> LogUtils.d("distinctUntilChanged = " + value));


        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .take(4)
                .subscribe(value -> LogUtils.d("take = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .takeLast(4)
                .subscribe(value -> LogUtils.d("takeLast = " + value));

        mDisposable = Observable.intervalRange(1, 6, 0, 900, TimeUnit.MILLISECONDS)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(value -> LogUtils.d("debounce = " + value));

        mDisposable = Observable.intervalRange(1, 6, 0, 900, TimeUnit.MILLISECONDS)
                .throttleWithTimeout(1, TimeUnit.SECONDS)
                .subscribe(value -> LogUtils.d("throttleWithTimeout = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .firstElement()
                .subscribe(value -> LogUtils.d("firstElement = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 3, 4, 5)
                .lastElement()
                .subscribe(value -> LogUtils.d("lastElement = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 6)
                .elementAt(3)
                .subscribe(value -> LogUtils.d("elementAt = " + value));

       /*会抛出异常
        mDisposable = Observable.just(1,2,3,4,5,6)
                .elementAtOrError(10)
                .subscribe(value->LogUtils.d("elementAtOrError = "+value));*/

        mDisposable = Observable.just(1, 2, 3, 4, 5, 6)
                .all(integer -> integer > 3)
                .subscribe(value -> LogUtils.d("all not meet = " + value));

        mDisposable = Observable.just(1, 2, 3, 4, 5, 6)
                .all(integer -> integer > 0)
                .subscribe(value -> LogUtils.d("all Meet = " + value));

        //判断第一个条件是否满足，如果不满足，则全部发送，如果满足，则该元素不发送，继续寻找下一个
        //一旦不满足，则后面的全部发送
        mDisposable = Observable.just(1, 2, 3, 4, 5, 6, 1, 2)
                .skipWhile(integer -> integer < 3)
                .subscribe(value -> LogUtils.d("skipWhile" + value));

        //一旦不满足，则后面的全都不发送
        mDisposable = Observable.just(1, 2, 3, 1, 2, 6)
                .takeWhile(integer -> integer > 3)
                .subscribe(value -> LogUtils.d("takeWhile" + value));

        //一旦满足，下一次事件将不会发送
        mDisposable = Observable.just(1, 2, 3, 4, 5, 6, 1, 2)
                .takeUntil(integer -> integer > 3)
                .subscribe(value -> LogUtils.d("takeUntil" + value));



        mDisposable = Observable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
                .skipUntil(Observable.intervalRange(1, 6, 3, 1, TimeUnit.SECONDS))
                .subscribe(value -> LogUtils.d("skipUntil = " + value));


    }


    public void showDialog(View view){
        TestDialog testDialog = new TestDialog();
        testDialog.show(getSupportFragmentManager(),"");
    }

    public void test(View view){
        Toast.makeText(this,"点击了",Toast.LENGTH_SHORT).show();
    }
}
