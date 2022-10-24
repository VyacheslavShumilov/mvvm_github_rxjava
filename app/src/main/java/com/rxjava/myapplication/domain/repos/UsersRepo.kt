package com.rxjava.myapplication.domain.repos

import com.rxjava.myapplication.domain.entities.UsersEntity
import io.reactivex.rxjava3.core.Single

//бизнес-логика. Описаны методы доступа к данным

interface UsersRepo {
    // Стандатрный интерфейс обращения к данным, отвечающий контракту CRUD
    // (-) Create
    // Read
    // (-) Update
    // (-) Delete

    // Read
    // асинхронный подход, два Callback'a
    fun getUsers(
        onSuccess: (List<UsersEntity>) -> Unit,  //функция ничего не возвращает
        onError: ((Throwable) -> Unit)? = null  //
    )


    fun getUsers(): Single<List<UsersEntity>>  //возвращает реактивный объект Single, на который можно подписаться

}