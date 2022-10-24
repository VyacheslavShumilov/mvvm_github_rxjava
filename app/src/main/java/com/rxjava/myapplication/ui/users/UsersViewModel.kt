package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.domain.repos.UsersRepo
import com.rxjava.myapplication.utils.SingleEventLiveData
import io.reactivex.rxjava3.kotlin.subscribeBy

// ViewModel решает особенность/проблему восстановления состояния (при повороте экрана)
// При применении ViewModel сохранилась проблема состояний при котором порядок вызова функций имеет значение
// Кроме того, LiveData не поддерживает из коробки режим одноразового события SingleEvent

class UsersViewModel (private val usersRepo: UsersRepo) : UsersContract.ViewModel {

    // если не прописать тип "LiveData<List<UsersEntity>>" наружу все будут думать, что это и есть MutableLiveData(). Полиморфизм в действии
    // LiveData кэширует значение, запоминает последнее переданное значение и все кто подписываются сразу получают это значение. Например, поворачиваю экран и получаю актуальное состояние, просто подписавшись заново на эти View
    // хранение реализовано ВНУТРИ LiveData. В отличие от Presenter состояния не храним, достаточно LiveData, которая заменяет собой View

    override val usersLiveData: LiveData<List<UsersEntity>> = MutableLiveData()
    override val errorLiveData: LiveData<Throwable> = SingleEventLiveData() // в качествк single event выступает errorLiveData
    override val progressLiveData: LiveData<Boolean> = MutableLiveData()

    //Особенность/ Проблема ViewModel: Переход с ПЕРВОЙ на ВТОРУЮ активити -> возвращаение на ПЕРВУЮ активити -> поворот экрана == появляется ВТОРАЯ активити (если указать "= MutableLiveData()")
    //Причина = не существует одноразовой LIveData. Решение: заменить "= MutableLiveData()" на "= SingleEventLiveData()"
    override val openProfileLiveData: LiveData<Unit> = SingleEventLiveData()

    override fun onRefresh() {
        loadData()
    }


    // Цепочка обработки клика на юзера в списке: ViewHolder -> Adapter -> Contract -> Activity -> ViewModel
    // ViewModel ловит клик на юреза из списка и принимает решение, что делать дальше (открываем/сохраняем/удаляем и т.п.).
    // Логика принятия решения лежит полностью на ViewModel

    // Для открытия новой Активити создавать интент тут не корректно. В идеале ViewModel не должна знать о классах андройда вроде Intent,
    // Кроме того ViewModel не должна ничего знать о Новой активити (ProfileActivity) и context иметь в ViewModel некорректно
    // Поэтому, нужно завести еще одну LiveData (openProfileLiveData)

    override fun onUserClick(usersEntity: UsersEntity) {
        openProfileLiveData.mutable().postValue(Unit) //нужно просто зажечь событие, передать Unit - ничего
    }

    private fun loadData() {
        progressLiveData.mutable().postValue(true)

        // Код при применении rxjava. Пробрасываю событие напрямую
        usersRepo.getUsers().subscribeBy(
            onSuccess = {
                progressLiveData.mutable().postValue(false)
                usersLiveData.mutable().postValue(it)
            },
            onError = {
                progressLiveData.mutable().postValue(false)
                errorLiveData.mutable().postValue(it)
            }
        )
        /* Код БЕЗ rx java
//        usersRepo.getUsers(
//            onSuccess = {
//                progressLiveData.mutable().postValue(false)
//                usersLiveData.mutable().postValue(it)
//            },
//            onError = {
//                progressLiveData.mutable().postValue(false)
//                errorLiveData.mutable().postValue(it)
//            }
//        )

 */
    }

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
}