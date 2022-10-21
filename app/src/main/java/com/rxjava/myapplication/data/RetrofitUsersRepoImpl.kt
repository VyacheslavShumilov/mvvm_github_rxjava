package com.rxjava.myapplication.data

import com.rxjava.myapplication.data.retrofit.GithubApi
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.domain.repos.UsersRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//СЛОЙ ДАННЫХ
//абстрактный источник данных, не зависит от базы данных. Чтобы Main Activity не знала, что конкретно используется Retrofit/OkHttp/др. библиотека, чтобы не переписывать пол проекта
//чтобы абстрагироваться от конкретной используемой библиотеки

class RetrofitUsersRepoImpl : UsersRepo {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api: GithubApi = retrofit.create(GithubApi::class.java)


    override fun getUsers(onSuccess: (List<UsersEntity>) -> Unit, onError: ((Throwable) -> Unit)?) {
        api.getUsers().enqueue(object : Callback<List<UsersEntity>> {
            override fun onResponse(
                call: Call<List<UsersEntity>>,
                response: Response<List<UsersEntity>>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    onSuccess.invoke(body)
                } else {
                    onError?.invoke(IllegalStateException("Данных нет или ошибка"))
                }
            }

            override fun onFailure(call: Call<List<UsersEntity>>, t: Throwable) {
                onError?.invoke(t)
            }

        })
    }

}