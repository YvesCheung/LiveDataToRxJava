@file:Suppress("NOTHING_TO_INLINE", "unused")

package android.arch.convert

import android.arch.lifecycle.LifecycleOwner
import io.reactivex.*

/**
 * Created by 张宇 on 2018/3/14.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
inline fun <reified T> Observable<T>.bindLifecycle(owner: LifecycleOwner): Observable<T> =
        LifecycleConvert.bindLifecycle(this, owner)

inline fun <reified T> Single<T>.bindLifecycle(owner: LifecycleOwner): Maybe<T> =
        LifecycleConvert.bindLifecycle(this, owner)

inline fun <reified T> Single<T>.bindLifecycleWithError(owner: LifecycleOwner): Single<T> =
        LifecycleConvert.bindLifecycleWithError(this, owner)

inline fun <reified T> Maybe<T>.bindLifecycle(owner: LifecycleOwner): Maybe<T> =
        LifecycleConvert.bindLifecycle(this, owner)

inline fun <reified T> Flowable<T>.bindLifecycle(owner: LifecycleOwner): Flowable<T> =
        LifecycleConvert.bindLifecycle(this, owner)

inline fun Completable.bindLifecycle(owner: LifecycleOwner): Completable =
        LifecycleConvert.bindLifecycle(this, owner)

inline fun Completable.bindLifecycleWithError(owner: LifecycleOwner): Completable =
        LifecycleConvert.bindLifecycleWithError(this, owner)