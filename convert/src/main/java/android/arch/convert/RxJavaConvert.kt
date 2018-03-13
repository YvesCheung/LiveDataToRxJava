package android.arch.convert

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables

/**
 * Created by 张宇 on 2018/3/12.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object RxJavaConvert {

    @JvmStatic
    fun <T> asObservable(liveData: LiveData<T>): Observable<T> {
        return LiveDataObservable(liveData)
    }

    @JvmStatic
    fun <T> asObservableAllowNull(liveData: LiveData<T>, valueIfNull: T): Observable<T> {
        return LiveDataObservable(liveData, valueIfNull)
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

    inner class RemoveObserverInMainThread(private val observer: io.reactivex.Observer<in T>)
        : MainThreadDisposable(), Observer<T> {

        override fun onChanged(t: T?) {
            if (!isDisposed) {
                if (t == null) {
                    if (valueIfNull != null) {
                        observer.onNext(valueIfNull)
                    } else {
                        observer.onError(NullPointerException("asObservable() onNext(t), t cannot be null"))
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

inline fun <reified T> LiveData<T>.asObservable(): Observable<T> =
        RxJavaConvert.asObservable(this)

inline fun <reified T> LiveData<T>.asObservableAllowNull(valueIfNull: T): Observable<T> =
        RxJavaConvert.asObservableAllowNull(this, valueIfNull)