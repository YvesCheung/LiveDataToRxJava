package android.arch.convertrxjava

import android.arch.convert.*
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    private val liveData = MyLiveData()

    init {
        val observable = liveData.toObservable().bindLifecycle(this).map { "observable-$it" }
        val single = liveData.toSingle().bindLifecycle(this).map { "single-$it" }
        val maybe = liveData.toMaybe().bindLifecycle(this).map { "maybe-$it" }
        val flowable = liveData.toFlowableAllowNull("null").bindLifecycle(this).map { "flowable-$it" }
        val completable = liveData.toCompletable().bindLifecycle(this)

        liveData.observe(this, Observer { s ->
            Log.i(TAG, "onChange $s")
        })
        observable.subscribe({ it ->
            Log.i(TAG, it)
        }, {
            Log.e(TAG, "$it")
        })
        single.subscribe({ it ->
            Log.i(TAG, it)
        }, {
            Log.e(TAG, "$it")
        })
        maybe.subscribe({ it ->
            Log.i(TAG, it)
        }, {
            Log.e(TAG, "$it")
        })
        flowable.subscribe({ it ->
            Log.i(TAG, it)
        }, {
            Log.e(TAG, "$it")
        })
        completable.subscribe({
            Log.i(TAG, "its complete")
        }, {
            Log.e(TAG, "$it")
        })
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
