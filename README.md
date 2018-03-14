# LiveData to RxJava

---

LiveDataToRxJava是一个语言扩展库，提供一些可以用于把[LiveData][1]转换成[RxJava][2]的接口。

使用
------

通过kotlin扩展函数，可以很方便地把LiveData转成RxJava

```Kotlin
val observable = liveData.toObservable()
val single = liveData.toSingle()
val maybe = liveData.toMaybe()
val flowable = liveData.toFlowable()
val completable = liveData.toCompletable()
```

如果是使用Java，Api就会略显得冗长一点

```Java
MutableLiveData<String> liveData = new MutableLiveData<>();

Completable completable = RxJavaConvert.toCompletable(liveData);
Observable<String> observable = RxJavaConvert.toObservable(liveData);
Flowable<String> flowable = RxJavaConvert.toFlowable(liveData);
Single<String> single = RxJavaConvert.toSingle(liveData);
Maybe<String> maybe = RxJavaConvert.toMaybe(liveData);
```

许可证
-------

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
