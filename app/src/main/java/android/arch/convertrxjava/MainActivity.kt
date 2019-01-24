package android.arch.convertrxjava

import android.arch.convert.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    private val liveData = MyLiveData()

    init {
        val observable = liveData.toObservable().bindLifecycle(this).map { "observable-$it" }
        val single = liveData.toSingle().bindLifecycle(this).map { "single-$it" }
        val maybe = liveData.toMaybe().bindLifecycle(this).map { "maybe-$it" }
        val flowable = liveData.toFlowableAllowNull("null").bindLifecycle(this).map { "flowable-$it" }
        val completable = liveData.toCompletable().bindLifecycle(this)

        val onSucess = Consumer<String> { s -> Log.i(TAG, s) }
        val onError = Consumer<Throwable> { e -> Log.i(TAG, "$e") }

        liveData.observe(this, Observer { Log.i(TAG, "onChange $it") })
        observable.subscribe(onSucess, onError)
        single.subscribe(onSucess, onError)
        maybe.subscribe(onSucess, onError)
        flowable.subscribe(onSucess, onError)
        completable.subscribe(Action { Log.i(TAG, "its complete") }, onError)
        LifecycleConvert.lifecycleObservable(this)
                .subscribe {
                    Log.e(TAG, it.toString())
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        liveData.value = "onPostCreate"
    }

    override fun onStart() {
        super.onStart()
        liveData.value = "onStart"
    }

    override fun onResume() {
        super.onResume()
        liveData.value = "onResume"
    }

    override fun onPause() {
        liveData.value = "onPause"
        super.onPause()
    }

    override fun onStop() {
        liveData.value = "onStop"
        super.onStop()
    }

    override fun onDestroy() {
        liveData.value = "onDestroy"
        super.onDestroy()
    }
}

class MyLiveData : MutableLiveData<String>() {

    override fun onActive() {
        super.onActive()
        Log.i(TAG, "onActive")
    }

    override fun onInactive() {
        super.onInactive()
        Log.i(TAG, "onInActive")
    }
}

private const val TAG = "LiveDataToRxJava"
