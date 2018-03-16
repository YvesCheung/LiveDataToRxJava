# LiveData to RxJava

---

LiveDataToRxJava是一个语言扩展库，提供一些可以用于把[LiveData][1]转换成[RxJava][2]的接口。

# 使用

### 直接转换成Reactive接口

通过 kotlin 扩展函数，可以很方便地把 ``LiveData`` 转成 ``RxJava``：

```Kotlin
val completable = liveData.toCompletable()
val observable = liveData.toObservable()
val flowable = liveData.toFlowable()
val single = liveData.toSingle()
val maybe = liveData.toMaybe()
```

如果是使用Java，Api就会略显得冗长一点：

```Java
MutableLiveData<String> liveData = new MutableLiveData<>();

Completable completable = RxJavaConvert.toCompletable(liveData);
Observable<String> observable = RxJavaConvert.toObservable(liveData);
Flowable<String> flowable = RxJavaConvert.toFlowable(liveData);
Single<String> single = RxJavaConvert.toSingle(liveData);
Maybe<String> maybe = RxJavaConvert.toMaybe(liveData);
```

### 订阅的生命周期

``LiveData`` 可以指定观察的期限在某一个 ``LifecycleOwner`` 的生命周期中。在 Android 26 以上的 support 包里，常用的 ``Activity`` 和 ``Fragment`` 都已经实现了 ``LifecycleOwner`` 这个接口，所以可以很方便地在ui有效的生命周期内使用我们的数据。

使用本库，可以通过下面的Api来把 ``LifecycleOwner`` 转换成一个 ``Observable``：

```Kotlin
//`this` can be an Activity or a Fragment
val observable: Observable<Lifecycle.Event> = LifecycleConvert.lifecycleObservable(this)
```

很多情况下，可以使用下面的Api来让你的可观察对象具有生命周期：

```Kotlin
//`this` can be an Activity or a Fragment
val observableWithLife = observable.bindLifecycle(this)
val singleWithLife = single.bindLifecycle(this)
val maybeWithLife = maybe.bindLifecycle(this)
val flowableWithLife = flowable.bindLifecycle(this)
val completableWithLife = completable.bindLifecycle(this)
```

``bindLifecycle`` 操作符可以让你的可观察对象具有与 ``LiveData.observe(this,{doSomething})`` 相同的生命周期，也就是从 ``onStart`` 状态到 ``onPause`` 状态。

> 绑定生命周期的操作符不仅适用于从 ``LiveData`` 转化而来的 ``Observable`` (或其他 ``Reactive`` 接口）。所以你也可以用这个库来为其他 ``RxJava`` 相关的行为绑定生命周期，比如 ``Retrofit`` 的网络请求，当ui不再活跃的时候可以自动 ``dispose`` 这个网络请求。

# 其他细节

### 空值

``LiveData`` 与 ``Reactive`` 接口有个很大的差异，就是 ``LiveData`` 允许传递null值，而 ``Reactive`` 规范是不可以的。所以在转换到 ``RxJava`` 的过程中null值就会很尴尬。默认的 ``toObservable`` 等接口在遇到null值时会抛出一个异常。下面是两个我对此的建议：

1. 定义一个代表null的常量或对象，在需要传递null值的地方使用该常量或对象。

2. 使用 ``toObservableAllowNull`` 系列的Api

```Kotlin
val observable = liveData.toObservableAllowNull(valueIfNull)
val single = liveData.toSingleAllowNull(valueIfNull)
val maybe = liveData.toMaybeAllowNull(valueIfNull)
val flowable = liveData.toFlowableAllowNull(valueIfNull)
val completable = liveData.toCompletableAllowNull()
```

# 许可证

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
