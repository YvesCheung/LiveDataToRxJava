@file:Suppress("unused")

package android.arch.convert

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable
import io.reactivex.functions.Predicate
import org.reactivestreams.Publisher
import java.util.concurrent.CancellationException

/**
 * Created by 张宇 on 2018/3/13.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object LifecycleConvert {

    @JvmStatic
    fun <T> bindLifecycle(observer: Observable<T>, owner: LifecycleOwner): Observable<T> =
            observer.compose(LifecycleConvert.bind(owner))

    @JvmStatic
    fun <T> bindLifecycle(single: Single<T>, owner: LifecycleOwner): Maybe<T> =
            single.toMaybe().compose(LifecycleConvert.bind(owner))

    @JvmStatic
    fun <T> bindLifecycleWithError(single: Single<T>, owner: LifecycleOwner): Single<T> =
            single.compose(LifecycleConvert.bind(owner))

    @JvmStatic
    fun <T> bindLifecycle(maybe: Maybe<T>, owner: LifecycleOwner): Maybe<T> =
            maybe.compose(LifecycleConvert.bind(owner))

    @JvmStatic
    fun <T> bindLifecycle(flowable: Flowable<T>, owner: LifecycleOwner): Flowable<T> =
            flowable.compose(LifecycleConvert.bind(owner))

    @JvmStatic
    fun bindLifecycle(completable: Completable, owner: LifecycleOwner): Completable =
            completable.bindLifecycleWithError(owner).onErrorComplete { it is CancellationException }

    @JvmStatic
    fun bindLifecycleWithError(completable: Completable, owner: LifecycleOwner): Completable =
            completable.compose(LifecycleConvert.bind<Nothing>(owner))

    @JvmStatic
    fun <T> bind(owner: LifecycleOwner): LifecycleTransformer<T> =
            LifecycleTransformer(LifecycleObservable(owner))

    @JvmStatic
    fun lifecycleObservable(owner: LifecycleOwner): Observable<Lifecycle.Event> =
            LifecycleObservable(owner)
}

internal class LifecycleObservable(private val owner: LifecycleOwner)
    : Observable<Lifecycle.Event>() {

    override fun subscribeActual(observer: Observer<in Lifecycle.Event>) {
        if (!checkMainThread(observer)) {
            return
        }
        val lifecycle = LifecycleObserver(observer)
        observer.onSubscribe(lifecycle)
        owner.lifecycle.addObserver(lifecycle)
    }

    inner class LifecycleObserver(private val observer: Observer<in Lifecycle.Event>)
        : MainThreadDisposable(), GenericLifecycleObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (!isDisposed) {
                observer.onNext(event)
            }
        }

        override fun onDispose() {
            owner.lifecycle.removeObserver(this)
        }
    }

    fun checkInActiveState() = owner.lifecycle.currentState == Lifecycle.State.DESTROYED

    fun checkInActive(event: Lifecycle.Event) = Lifecycle.Event.ON_STOP == event

    fun shouldBeActive() = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)

    fun isAttachedTo(owner: LifecycleOwner) = this.owner === owner
}

class LifecycleTransformer<T> internal constructor(
        private val observable: LifecycleObservable
) : ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {

    private val inactive = observable.filter { observable.checkInActive(it) }
    private val active get() = observable.shouldBeActive()

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.filter { active }.takeUntil(inactive)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.filter { active }.takeUntil(inactive.toFlowable(BackpressureStrategy.LATEST))
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.filterOrNever(Predicate { active }).takeUntil(inactive.firstOrError())
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.filter { active }.takeUntil(inactive.firstElement())
    }

    override fun apply(upstream: Completable): CompletableSource {
        val completableAfterActive = upstream.andThen {
            if (active) {
                it.onComplete()
            } else {
                it.onError(CancellationException("complete before active"))
            }
        }
        val completableBeforeInactive = inactive.flatMapCompletable { Completable.error(CancellationException()) }
        return Completable.ambArray(completableAfterActive, completableBeforeInactive)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val that = other as? LifecycleTransformer<*>
        return observable == that?.observable
    }

    override fun hashCode(): Int {
        return observable.hashCode()
    }

    override fun toString(): String {
        return "LifecycleTransformer{" +
                "observable=" + observable +
                '}'.toString()
    }
}