package com.rxjava.myapplication.domain.repos

import com.rxjava.myapplication.domain.entities.UserEntity

interface UsersRepo {
    // C_R_UD
    // (-) Create
    // Read
    // (-) Update
    // (-) Delete

    // Read
    fun getUsers(
        onSuccess: (List<UserEntity>) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    )

}