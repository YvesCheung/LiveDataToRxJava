package android.arch.convertrxjava;

import android.arch.convert.LifecycleConvert;
import android.arch.convert.RxJavaConvert;
import androidx.lifecycle.MutableLiveData;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by 张宇 on 2018/3/14.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */

public class JavaActivity extends AppCompatActivity {

    MutableLiveData<String> liveData = new MyLiveData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Completable completable = RxJavaConvert.toCompletable(liveData);
        Observable<String> observable = RxJavaConvert.toObservable(liveData);
        Flowable<String> flowable = RxJavaConvert.toFlowable(liveData);
        Single<String> single = RxJavaConvert.toSingle(liveData);
        Maybe<String> maybe = RxJavaConvert.toMaybe(liveData);

        Completable completableWithLife = LifecycleConvert.bindLifecycle(completable, this);
        Observable<String> observableWithLife = LifecycleConvert.bindLifecycle(observable, this);
        Flowable<String> flowableWithLife = LifecycleConvert.bindLifecycle(flowable, this);
        Maybe<String> singleWithLife = LifecycleConvert.bindLifecycle(single, this);
        Single<String> singleWithLifeAndError = LifecycleConvert.bindLifecycleWithError(single, this);
        Maybe<String> maybeWithLife = LifecycleConvert.bindLifecycle(maybe, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        liveData.setValue("onStart");
    }

    @Override
    protected void onPause() {
        liveData.setValue("onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        liveData.setValue("onDestroy");
        super.onDestroy();
    }
}
