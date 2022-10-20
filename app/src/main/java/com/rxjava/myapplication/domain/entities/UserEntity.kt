package com.rxjava.myapplication.domain.entities

import com.google.gson.annotations.SerializedName

// бизнес-логика.
// DOMAIN (МОДЕЛЬ) не зависит от DATA (ДАННЫЕ) и UI.
// DATA (ДАННЫЕ) зависит от DOMAIN (МОДЕЛЬ). НЕ наоборот
// UI зависит от DOMAIN (МОДЕЛЬ) и не зависит от DATA (ДАННЫЕ). Чтобы UI не зависел от DATA введен класс Application


data class UserEntity(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String
)