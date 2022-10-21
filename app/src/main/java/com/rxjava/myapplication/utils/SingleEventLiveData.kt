package com.rxjava.myapplication.utils

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

// LiveData получает значение и хранит его -> восстановленние данных после поворота экрана.
// Но в ViewModel нет ссылки на View -> вопрос с передачей во View одноразового события, кторое НЕ нужно восстанавливать при повороте экрана
// Для решения этой проблемы создается класс SingleEventLiveData. У этой LiveData метод при обработке всегда отдает значение один раз...
// ...метод не будет больше кэшировать это значение

class SingleEventLiveData<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)      //3) делаю это значение false. Это значит значение не новое  (переменная pending ожидающая потокобезопасный boolean)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Наследование от Observer и подсунуть другой. Обертка вокруг Observer, который будет проверять отправлено значение или нет. Если что-то происходит оповещение
        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false)) { // 2) как только оно прочитано 4) если значение не новое, оно проверку не проходит и не оптравляется
                observer.onChanged(t)       // 5) событие будет отправлено один единственный раз, тому кто этого хочет
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)       // 1) Если setValue() случилось новое значение, ожидаю его отправки. Говорою, что это новое значение
        super.setValue(t)
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}