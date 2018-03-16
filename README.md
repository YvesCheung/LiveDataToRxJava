# LiveData to RxJava

---

LiveDataToRxJava is a language extension library that provides some API for converting [LiveData][1] to [RxJava][2].

[![](https://jitpack.io/v/YvesCheung/LiveDataToRxJava.svg)](https://jitpack.io/#YvesCheung/LiveDataToRxJava)

[中文版README](README_CN.md)

# Usage

### Convert to Reactive interface

With the kotlin extension function, you can easily convert ``LiveData`` to ``RxJava``:

```Kotlin
val completable = liveData.toCompletable()
val observable = liveData.toObservable()
val flowable = liveData.toFlowable()
val single = liveData.toSingle()
val maybe = liveData.toMaybe()
```

If you are using Java, Api will be slightly longer:

```Java
MutableLiveData<String> liveData = new MutableLiveData<>();

Completable completable = RxJavaConvert.toCompletable(liveData);
Observable<String> observable = RxJavaConvert.toObservable(liveData);
Flowable<String> flowable = RxJavaConvert.toFlowable(liveData);
Single<String> single = RxJavaConvert.toSingle(liveData);
Maybe<String> maybe = RxJavaConvert.toMaybe(liveData);
```

### Lifecycle

``LiveData`` can specify the period of observation in the lifecycle of a ``LifecycleOwner``. In our applications including Android support-26 library, the commonly used ``Activity`` and ``Fragment`` has implemented the ``LifecycleOwner`` interface, so it can be very convenient to use our data in the lifecycle of UI.

The following code can be used to convert ``LifecycleOwner`` into a ``Observable``:

```Kotlin
//`this` can be an Activity or a Fragment
val observable: Observable<Lifecycle.Event> = LifecycleConvert.lifecycleObservable(this)
```

More often, you can obtain a observable with lifecycle:

```Kotlin
//`this` can be an Activity or a Fragment
val observableWithLife = observable.bindLifecycle(this)
val singleWithLife = single.bindLifecycle(this)
val maybeWithLife = maybe.bindLifecycle(this)
val flowableWithLife = flowable.bindLifecycle(this)
val completableWithLife = completable.bindLifecycle(this)
```

The ``bindLifecycle`` operator allows your observable to have the same lifecycle as ``LiveData.observe (this, {doSomething})`` , that is, from ``onStart`` state to ``onPause`` state.

> Binding Lifecycle Operators **Not only** Applies to ``Observable`` (or other ``Reactive`` interfaces) converted from ``LiveData``. So you can also use this library for other ``RxJava`` behavior binding lifecycle, such as ``Retrofit`` network requests, which automatically ``dispose`` those requests when ui is no longer active.

# Other details

### Null value

There is a difference between ``LiveData`` and ``Reactive`` interface. ``LiveData`` can pass null value, but the ``Reactive`` interface can't. So the null value is awkward while converting to ``RxJava``. The ``toObservable`` operator throws an exception when it encounters a null value. Here are a few of my suggestions for this:

1. Define a constant or object that represents null, and use it instead.
2. Use Java8 ``Optional`` to wrap a nullable object.
3. Use ``toObservableAllowNull`` APIs

```Kotlin
val observable = liveData.toObservableAllowNull(valueIfNull)
val single = liveData.toSingleAllowNull(valueIfNull)
val maybe = liveData.toMaybeAllowNull(valueIfNull)
val flowable = liveData.toFlowableAllowNull(valueIfNull)
val completable = liveData.toCompletableAllowNull()
```

### Values emitted out of the lifecycle

These values will be ignored when ``Flowable`` or ``Observable`` emitts values outside the lifecycle.  But for ``Single``, the only poor value, if ignored, is ``Maybe`` :

```Kotlin
val maybe = single.bindLifecycle(this)
```

And if you still want to operate on ``Single``, you can do this:

```Kotlin
val singleWithLife = single.bindLifecycleWithError(this)
```

A ``CancellationException`` will be thrown when values are emitted outside the lifecycle. ``Completable`` is the same.

# Download

Add it in your root build.gradle at the end of repositories:
```Groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency：
```Groovy
dependencies {
	compile 'com.github.YvesCheung:LiveDataToRxJava:v1.1'
}
```

# License

       Copyright 2018 Yves Cheung
    
       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at
    
           http://www.apache.org/licenses/LICENSE-2.0
    
       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

   


  [1]: https://developer.android.com/topic/libraries/architecture/livedata.html
  [2]: https://github.com/ReactiveX/RxJava
