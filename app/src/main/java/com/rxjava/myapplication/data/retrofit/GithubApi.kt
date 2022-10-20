package com.rxjava.myapplication.data.retrofit

import com.rxjava.myapplication.domain.entities.UsersEntity
import retrofit2.Call
import retrofit2.http.GET

//СЛОЙ ДАННЫХ. Зависит от DOMAIN (ДАННЫХ) UI


interface GithubApi {
    @GET("users")
    fun getUsers(): Call<List<UsersEntity>>
}