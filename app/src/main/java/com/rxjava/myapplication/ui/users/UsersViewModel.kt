package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.domain.repos.UsersRepo
import com.rxjava.myapplication.utils.SingleEventLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

// ViewModel решает особенность/проблему восстановления состояния (при повороте экрана)
// При применении ViewModel сохранилась проблема состояний при котором порядок вызова функций имеет значение
// Кроме того, LiveData не поддерживает из коробки режим одноразового события SingleEvent

class UsersViewModel (private val usersRepo: UsersRepo) : UsersContract.ViewModel {

    // 2022.10.24 c RxJava
    override val usersLiveData: Observable<List<UsersEntity>> = BehaviorSubject.create()
    override val errorLiveData: Observable<Throwable> = BehaviorSubject.create() // в качествк single event выступает errorLiveData
    override val progressLiveData: Observable<Boolean> = BehaviorSubject.create()
    override val openProfileLiveData: Observable<Unit> = BehaviorSubject.create()

    /* 2022.10.20 без RxJava
    // если не прописать тип "LiveData<List<UsersEntity>>" наружу все будут думать, что это и есть MutableLiveData(). Полиморфизм в действии
    // LiveData кэширует значение, запоминает последнее переданное значение и все кто подписываются сразу получают это значение. Например, поворачиваю экран и получаю актуальное состояние, просто подписавшись заново на эти View
    // хранение реализовано ВНУТРИ LiveData. В отличие от Presenter состояния не храним, достаточно LiveData, которая заменяет собой View

    override val usersLiveData: LiveData<List<UsersEntity>> = MutableLiveData()
    override val errorLiveData: LiveData<Throwable> = SingleEventLiveData() // в качествк single event выступает errorLiveData
    override val progressLiveData: LiveData<Boolean> = MutableLiveData()

    //Особенность/ Проблема ViewModel: Переход с ПЕРВОЙ на ВТОРУЮ активити -> возвращаение на ПЕРВУЮ активити -> поворот экрана == появляется ВТОРАЯ активити (если указать "= MutableLiveData()")
    //Причина = не существует одноразовой LIveData. Решение: заменить "= MutableLiveData()" на "= SingleEventLiveData()"
    override val openProfileLiveData: LiveData<Unit> = SingleEventLiveData()
    */


    // Цепочка обработки клика на юзера в списке: ViewHolder -> Adapter -> Contract -> Activity -> ViewModel
    // ViewModel ловит клик на юреза из списка и принимает решение, что делать дальше (открываем/сохраняем/удаляем и т.п.).
    // Логика принятия решения лежит полностью на ViewModel

    // Для открытия новой Активити создавать интент тут не корректно. В идеале ViewModel не должна знать о классах андройда вроде Intent,
    // Кроме того ViewModel не должна ничего знать о Новой активити (ProfileActivity) и context иметь в ViewModel некорректно
    // Поэтому, нужно завести еще одну LiveData (openProfileLiveData)

    override fun onUserClick(usersEntity: UsersEntity) {
        openProfileLiveData
    //openProfileLiveData.mutable().postValue(Unit) // (без RxJava) нужно просто зажечь событие, передать Unit - ничего
    }

    private fun loadData() {
        progressLiveData.mutable().onNext(true)

        // Код при применении rxjava. Пробрасываю событие напрямую
        usersRepo.getUsers()
            // без строки .observeOn(AndroidSchedulers.mainThread()), приложение не запуститься, т.к. события приходят из BehaviorSubject (см. выше). Загрузка из фонового потока - нужно переключиться на главный поток
            // Для этого указываю, что последующий код - цепочки (все что ниже) будут выполняться на главном потоке. Schedulers - сущность, оперирующая потоками.
            // Переключением потоков должна заниматься ViewModel либо сторонняя библиотека
            // Доп. инфа - перед тем, как делать subscribe(), можно добавить различные операторы преобразования данных через "." (один тип данных привести к другому типу, внести изменения). observeOn можно вызывать много раз
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
            onSuccess = {
                progressLiveData.mutable().onNext(false)
                usersLiveData.mutable().onNext(it)
            },
            onError = {
                progressLiveData.mutable().onNext(false)
                errorLiveData.mutable().onNext(it)
            }
        )

        /* 2022.10.20 Без RxJava
        usersRepo.getUsers(
            onSuccess = {
                progressLiveData.mutable().postValue(false)
                usersLiveData.mutable().postValue(it)
            },
            onError = {
                progressLiveData.mutable().postValue(false)
                errorLiveData.mutable().postValue(it)
            }
        )
        */
    }

    // 2022.10.24 c RxJava
    // Extension видимый внутри ViewModel - функция превращает Observable в Subject
    private fun <T : Any> Observable<T>.mutable(): Subject<T> {
        return this as? Subject<T>
            ?: throw IllegalStateException("It is not MutableLiveData") // в реаьности такое исключение не выпадет
    }


    /* 2022.10.20 без RxJava
    // Extension видимый внутри ViewModel - функция превращает LiveData в MutableLiveData
    // Оснонвые MutableLiveData и MediatorLivedata. LiveData наследник MutableLiveData
    // MediatorLivedata служит для объединения

    private fun <T> LiveData<T>.mutable(): MutableLiveData<T> {
        return this as? MutableLiveData<T>
            ?: throw IllegalStateException("It is not MutableLiveData") // в реаьности такое исключение не выпадет
    }

    // Вариант для сокращения кода. Вместо для каждой LiveData:
    // private val _usersLiveData: MutableLiveData<List<UsersEntity>>()
    // override val usersLiveData: LiveData<List<UsersEntity>>
    // get() = _usersLiveData
     */


    override fun onRefresh() {
        loadData()
    }
}