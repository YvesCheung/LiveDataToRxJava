@file:Suppress("NOTHING_TO_INLINE", "unused")

package android.arch.convert

import android.arch.convert.LifecycleConvert.bind
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import org.reactivestreams.Publisher

/**
 * Created by 张宇 on 2018/3/13.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object LifecycleConvert {

    fun <T> bind(owner: LifecycleOwner): LifecycleTransformer<T> =
            LifecycleTransformer(LifecycleObservable(owner))

    class LifecycleObservable(private val owner: LifecycleOwner) : Observable<Lifecycle.Event>() {

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

        fun checkInActive() = owner.lifecycle.currentState == Lifecycle.State.DESTROYED

        fun shouldBeActive() = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        fun isAttachedTo(owner: LifecycleOwner) = this.owner === owner
    }

    class LifecycleTransformer<T>(private val observable: LifecycleObservable)
        : ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            SingleTransformer<T, T>,
            MaybeTransformer<T, T>,
            CompletableTransformer {

        private val inactive = observable.filter { observable.checkInActive() }
        private val active = Predicate<T> { observable.shouldBeActive() }

        override fun apply(upstream: Observable<T>): ObservableSource<T> {
            return upstream.filter(active).takeUntil(inactive)
        }

        override fun apply(upstream: Flowable<T>): Publisher<T> {
            return upstream.filter(active).takeUntil(inactive.toFlowable(BackpressureStrategy.LATEST))
        }

        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream.filterOrNever(active).takeUntil(inactive.firstOrError())
        }

        override fun apply(upstream: Maybe<T>): MaybeSource<T> {
            return upstream.filter(active).takeUntil(inactive.firstElement())
        }

        override fun apply(upstream: Completable): CompletableSource {
            return Completable.create { emitter ->

                var upstreamDisposable: Disposable? = null
                var lifecycleDisposable: Disposable? = null

                upstreamDisposable = upstream.subscribe({
                    if (observable.shouldBeActive()) {
                        if (lifecycleDisposable?.isDisposed == false) {
                            lifecycleDisposable?.dispose()
                        }
                        emitter.onComplete()
                    }
                }, {
                    if (observable.shouldBeActive()) {
                        if (lifecycleDisposable?.isDisposed == false) {
                            lifecycleDisposable?.dispose()
                        }
                        emitter.onError(it)
                    }
                })

                lifecycleDisposable = inactive.subscribe {
                    if (upstreamDisposable?.isDisposed == false) {
                        upstreamDisposable?.dispose()
                    }
                }

                emitter.setCancellable {
                    if (upstreamDisposable?.isDisposed == false) {
                        upstreamDisposable?.dispose()
                    }
                    if (lifecycleDisposable?.isDisposed == false) {
                        lifecycleDisposable?.dispose()
                    }
                }
            }
        }

        inner class ActiveCompletable(val upstream: Completable) : Completable() {

            var upstreamDisposable: Disposable? = null
            var lifecycleDisposable: Disposable? = null

            override fun subscribeActual(emitter: CompletableObserver) {

                upstreamDisposable = upstream.subscribe({
                    if (observable.shouldBeActive()) {
                        if (lifecycleDisposable?.isDisposed == false) {
                            lifecycleDisposable?.dispose()
                        }
                        emitter.onComplete()
                    }
                }, {
                    if (observable.shouldBeActive()) {
                        if (lifecycleDisposable?.isDisposed == false) {
                            lifecycleDisposable?.dispose()
                        }
                        emitter.onError(it)
                    }
                })

                lifecycleDisposable = inactive.subscribe {
                    if (upstreamDisposable?.isDisposed == false) {
                        upstreamDisposable?.dispose()
                    }
                }

                emitter.onSubscribe(object : Disposable {
                    override fun isDisposed(): Boolean {

                    }

                    override fun dispose() {
                    }
                })
            }
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
}

inline fun <T> Observable<T>.bindLifecycle(owner: LifecycleOwner): Observable<T> = compose(bind(owner))

inline fun <T> Single<T>.bindLifecycle(owner: LifecycleOwner): Single<T> = compose(bind(owner))

inline fun <T> Maybe<T>.bindLifecycle(owner: LifecycleOwner): Maybe<T> = compose(bind(owner))

inline fun <T> Flowable<T>.bindLifecycle(owner: LifecycleOwner): Flowable<T> = compose(bind(owner))

inline fun Completable.bindLifecycle(owner: LifecycleOwner): Completable = this.compose(bind<Nothing>(owner))

