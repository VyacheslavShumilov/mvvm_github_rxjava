package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import com.rxjava.myapplication.domain.entities.UsersEntity

interface UsersContract {
    interface ViewModel {
        val usersLiveData: LiveData<List<UsersEntity>>
        val errorLiveData: LiveData<Throwable>
        val progressLiveData: LiveData<Boolean>
        val openProfileLiveData: LiveData<Unit>

        fun onRefresh()
        fun onUserClick(usersEntity: UsersEntity)
    }

}