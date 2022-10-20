package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import com.rxjava.myapplication.domain.entities.UserEntity

interface UsersContract {
    interface ViewModel {
        val usersLiveData: LiveData<List<UserEntity>>
        val errorLiveData: LiveData<Throwable>
        val progressLiveData: LiveData<Boolean>
        val openProfileLiveData: LiveData<Unit>

        fun onRefresh()
        fun onUserClick(userEntity: UserEntity)
    }

}