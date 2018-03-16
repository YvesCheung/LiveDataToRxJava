# LiveData to RxJava

---

LiveDataToRxJava是一个语言扩展库，提供一些接口可以把[LiveData][1]转换成[RxJava][2]。

[![](https://jitpack.io/v/YvesCheung/LiveDataToRxJava.svg)](https://jitpack.io/#YvesCheung/LiveDataToRxJava)

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

如果是使用 Java，Api会略微冗长一点：

```Java
MutableLiveData<String> liveData = new MutableLiveData<>();

Completable completable = RxJavaConvert.toCompletable(liveData);
Observable<String> observable = RxJavaConvert.toObservable(liveData);
Flowable<String> flowable = RxJavaConvert.toFlowable(liveData);
Single<String> single = RxJavaConvert.toSingle(liveData);
Maybe<String> maybe = RxJavaConvert.toMaybe(liveData);
```

### 订阅的生命周期

``LiveData`` 可以指定观察的期限在某一个 ``LifecycleOwner`` 的生命周期中。在 Android support-26 以上的包里，常用的 ``Activity`` 和 ``Fragment`` 都已经实现了 ``LifecycleOwner`` 这个接口，所以可以很方便地在 ui 有效的生命周期内使用我们的数据。

可以通过下面的Api来把 ``LifecycleOwner`` 转换成一个 ``Observable``：

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

> 绑定生命周期的操作符 **不仅** 适用于从 ``LiveData`` 转化而来的 ``Observable`` (或其他 ``Reactive`` 接口）。所以你也可以用这个库来为其他 ``RxJava`` 相关的行为绑定生命周期，比如 ``Retrofit`` 的网络请求，当 ui 不再活跃的时候可以自动 ``dispose`` 这个网络请求。

# 其他细节

### 空值

``LiveData`` 与 ``Reactive`` 接口有个很大的差异，就是 ``LiveData`` 允许传递null值，而 ``Reactive`` 规范是不可以的。所以在转换到 ``RxJava`` 的过程中null值就会很尴尬。默认的 ``toObservable`` 等接口在遇到null值时会抛出一个异常。下面是几个我对此的建议：

1. 定义一个代表null的常量或对象，在需要传递null值的地方使用该常量或对象。
2. 使用Java8 Optional类包装
3. 使用 ``toObservableAllowNull`` 系列的Api

```Kotlin
val observable = liveData.toObservableAllowNull(valueIfNull)
val single = liveData.toSingleAllowNull(valueIfNull)
val maybe = liveData.toMaybeAllowNull(valueIfNull)
val flowable = liveData.toFlowableAllowNull(valueIfNull)
val completable = liveData.toCompletableAllowNull()
```

### 非线程安全

``LiveData`` 是非线程安全的。``observe`` ， ``observeForever`` ， ``removeObserver`` ， ``setValue`` 等主要方法都需要在主线程上调用。这意味着转换成 ``Observable`` 接口之后还是必须在主线程中订阅。这是令人遗憾但暂时无法避免的。

### 生命周期以外的值传递

当 ``Flowable`` 或者 ``Observable`` 在生命周期之外期间发射值的时候，这些值会被无视掉。但是对于 ``Single`` 来说，那唯一可怜的值如果被无视掉的话，就相当于是 ``Maybe`` 了：

```Kotlin
val maybe = single.bindLifecycle(this)
```

而如果还是希望绑定生命周期之后仍然是对 ``Single`` 进行操作，可以这样：

```Kotlin
val singleWithLife = single.bindLifecycleWithError(this)
```

在生命周期以外发射值的话，会抛出一个 ``CancellationException`` 。 ``Completable`` 是一样道理的。

# 配置

在项目根目录build.gradle添加：
```Groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

在需要使用该库的module的build.gradle添加：
```Groovy
dependencies {
	compile 'com.github.YvesCheung:LiveDataToRxJava:v1.1'
}
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
