@file:Suppress("unused")

package android.arch.convert

import android.arch.lifecycle.LiveData

/**
 * Created by 张宇 on 2018/3/14.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */

inline fun <reified T> LiveData<T>.toObservable() = RxJavaConvert.toObservable(this)

inline fun <reified T> LiveData<T>.toObservableAllowNull(valueIfNull: T) =
        RxJavaConvert.toObservableAllowNull(this, valueIfNull)

inline fun <reified T> LiveData<T>.toFlowable() = RxJavaConvert.toFlowable(this)

inline fun <reified T> LiveData<T>.toFlowableAllowNull(valueIfNull: T) =
        RxJavaConvert.toFlowableAllowNull(this, valueIfNull)

inline fun <reified T> LiveData<T>.toSingle() = RxJavaConvert.toSingle(this)

inline fun <reified T> LiveData<T>.toSingleAllowNull(valueIfNull: T) =
        RxJavaConvert.toSingleAllowNull(this, valueIfNull)

inline fun <reified T> LiveData<T>.toMaybe() = RxJavaConvert.toMaybe(this)

inline fun <reified T> LiveData<T>.toMaybeAllowNull(valueIfNull: T) =
        RxJavaConvert.toMaybeAllowNull(this, valueIfNull)

inline fun <reified T> LiveData<T>.toCompletable() = RxJavaConvert.toCompletable(this)

inline fun <reified T> LiveData<T>.toCompletableAllowNull() = RxJavaConvert.toCompletableAllowNull(this)