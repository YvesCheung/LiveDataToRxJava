package android.arch.convertrxjava

import android.arch.convert.asObservable
import android.arch.convert.bind
import android.arch.convert.bindLifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Completable

class MainActivity : AppCompatActivity() {

    val livedata = MyLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        livedata.value = "onCreate"
        val observable = livedata.asObservable().bindLifecycle(this)
        observable.subscribe {
            Log.i("zycheck", "onNext $it")
        }
        livedata.observeForever(Observer<String> { s ->
            Log.i("zycheck", "onChange $s")
        })
    }

    override fun onStart() {
        super.onStart()
        livedata.value = "onStart"
    }

    override fun onResume() {
        super.onResume()
        livedata.value = "onResume"
    }

    override fun onPause() {
        super.onPause()
        livedata.value = "onPause"
    }

    override fun onStop() {
        livedata.value = "onStop"
        super.onStop()
    }

    override fun onDestroy() {
        livedata.value = "onDestroy"
        super.onDestroy()
    }
}

class MyLiveData : MutableLiveData<String>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<String>) {
        super.observe(owner, observer)
    }
    override fun onActive() {
        super.onActive()
        Log.i("zycheck", "onActive")
    }

    override fun onInactive() {
        super.onInactive()
        Log.i("zycheck", "onInActive")

    }
}
