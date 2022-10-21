package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.domain.repos.UsersRepo
import com.rxjava.myapplication.utils.SingleEventLiveData

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
    override val openProfileLiveData: LiveData<Unit> = SingleEventLiveData()

    override fun onRefresh() {
        loadData()
    }

    override fun onUserClick(usersEntity: UsersEntity) {
        openProfileLiveData.mutable().postValue(Unit)
    }

    private fun loadData() {
        progressLiveData.mutable().postValue(true)
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