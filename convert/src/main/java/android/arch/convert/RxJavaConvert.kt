@file:Suppress("unused")

package android.arch.convert

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable

/**
 * Created by 张宇 on 2018/3/12.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object RxJavaConvert {

    @JvmStatic
    fun <T> toObservable(liveData: LiveData<T>): Observable<T> {
        return LiveDataObservable(liveData)
    }

    @JvmStatic
    fun <T> toObservableAllowNull(liveData: LiveData<T>, valueIfNull: T): Observable<T> {
        return LiveDataObservable(liveData, valueIfNull)
    }

    @JvmStatic
    fun <T> toFlowable(liveData: LiveData<T>): Flowable<T> {
        return LiveDataObservable(liveData).toFlowable(BackpressureStrategy.LATEST)
    }

    @JvmStatic
    fun <T> toFlowableAllowNull(liveData: LiveData<T>, valueIfNull: T): Flowable<T> {
        return LiveDataObservable(liveData, valueIfNull).toFlowable(BackpressureStrategy.LATEST)
    }

    @JvmStatic
    fun <T> toSingle(liveData: LiveData<T>): Single<T> {
        return LiveDataObservable(liveData).firstOrError()
    }

    @JvmStatic
    fun <T> toSingleAllowNull(liveData: LiveData<T>, valueIfNull: T): Single<T> {
        return LiveDataObservable(liveData, valueIfNull).firstOrError()
    }

    @JvmStatic
    fun <T> toMaybe(liveData: LiveData<T>): Maybe<T> {
        return LiveDataObservable(liveData).firstElement()
    }

    @JvmStatic
    fun <T> toMaybeAllowNull(liveData: LiveData<T>, valueIfNull: T): Maybe<T> {
        return LiveDataObservable(liveData, valueIfNull).firstElement()
    }

    @JvmStatic
    fun <T> toCompletable(liveData: LiveData<T>): Completable {
        return LiveDataCompletable(liveData)
    }

    @JvmStatic
    fun <T> toCompletableAllowNull(liveData: LiveData<T>): Completable {
        return LiveDataCompletable(liveData, true)
    }
}

private class LiveDataObservable<T>(
        private val liveData: LiveData<T>,
        private val valueIfNull: T? = null
) : Observable<T>() {

    override fun subscribeActual(observer: io.reactivex.Observer<in T>) {
        if (!checkMainThread(observer)) {
            return
        }
        val relay = RemoveObserverInMainThread(observer)
        observer.onSubscribe(relay)
        liveData.observeForever(relay)
    }

    private inner class RemoveObserverInMainThread(private val observer: io.reactivex.Observer<in T>)
        : MainThreadDisposable(), Observer<T> {

        override fun onChanged(t: T?) {
            if (!isDisposed) {
                if (t == null) {
                    if (valueIfNull != null) {
                        observer.onNext(valueIfNull)
                    } else {
                        observer.onError(ReactiveStreamNullElementException(
                                "convert liveData value t to RxJava onNext(t), t cannot be null"))
                    }
                } else {
                    observer.onNext(t)
                }
            }
        }

        override fun onDispose() {
            liveData.removeObserver(this)
        }
    }
}

private class LiveDataCompletable<T>(
        private val liveData: LiveData<T>,
        private val allowNull: Boolean = false) : Completable() {

    override fun subscribeActual(s: CompletableObserver) {
        val relay = CompleteObserver(s)
        s.onSubscribe(relay)
        liveData.observeForever(relay)
    }

    private inner class CompleteObserver(val s: CompletableObserver)
        : MainThreadDisposable(), Observer<T> {

        override fun onDispose() {
            liveData.removeObserver(this)
        }

        override fun onChanged(t: T?) {
            if (!isDisposed) {
                if (t != null || allowNull) {
                    s.onComplete()
                } else {
                    s.onError(ReactiveStreamNullElementException(
                            "convert liveData value t to RxJava onNext(t), t cannot be null"))
                }
                dispose()
            }
        }
    }
}