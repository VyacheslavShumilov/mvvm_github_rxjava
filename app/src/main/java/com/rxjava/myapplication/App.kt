package com.rxjava.myapplication

import android.app.Application
import android.content.Context
import com.rxjava.myapplication.data.RetrofitUsersRepoImpl
import com.rxjava.myapplication.domain.repos.UsersRepo

class App : Application() {
    val usersRepo: UsersRepo by lazy { RetrofitUsersRepoImpl() }
}

val Context.app: App get() = applicationContext as App