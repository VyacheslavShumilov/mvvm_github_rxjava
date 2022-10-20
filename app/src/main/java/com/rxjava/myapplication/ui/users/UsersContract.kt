package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import com.rxjava.myapplication.domain.entities.UsersEntity

interface UsersContract {
    interface ViewModel {
        // View сама подписывается на ViewModel и ждет когда у нее изменится значение
        // Если наружу из ViewModel можно отдать только LiveData (неизменяемую - нельзя установить значение postValue() или setValue(), только получить). Иначе (если MutableLiveData) View может постить в ViewModel - реализует логику - нельзя!
        // у MutableLiveData в отличие от LiveData методы открыты наружу postValue() и setValue(). У LiveData эти методы protected

        val usersLiveData: LiveData<List<UsersEntity>>
        val errorLiveData: LiveData<Throwable>
        val progressLiveData: LiveData<Boolean>
        val openProfileLiveData: LiveData<Unit>

        fun onRefresh()     //управляющий метод
        fun onUserClick(usersEntity: UsersEntity)
    }

}