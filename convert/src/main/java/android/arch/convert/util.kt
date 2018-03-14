package android.arch.convert

import android.os.Looper
import io.reactivex.disposables.Disposables

/**
 * Created by 张宇 on 2018/3/13.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal fun checkMainThread(observer: io.reactivex.Observer<*>): Boolean {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        observer.onSubscribe(Disposables.empty())
        observer.onError(IllegalStateException(
                "Expected to be called on the main thread but was " + Thread.currentThread().name))
        return false
    }
    return true
}

class ReactiveStreamNullElementException(detail: String) : NullPointerException(detail)
