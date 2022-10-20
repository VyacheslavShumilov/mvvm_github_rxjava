package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.domain.repos.UsersRepo
import com.rxjava.myapplication.utils.SingleEventLiveData

class UsersViewModel (
    private val usersRepo: UsersRepo
) : UsersContract.ViewModel {

    override val usersLiveData: LiveData<List<UsersEntity>> = MutableLiveData()
    override val errorLiveData: LiveData<Throwable> = SingleEventLiveData() // single event
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

    private fun <T> LiveData<T>.mutable(): MutableLiveData<T> {
        return this as? MutableLiveData<T>
            ?: throw IllegalStateException("It is not MutableLiveData o_O")
    }
}