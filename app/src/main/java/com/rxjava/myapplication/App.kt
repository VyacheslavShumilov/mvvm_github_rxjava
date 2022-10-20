package com.rxjava.myapplication

import android.app.Application
import android.content.Context
import com.rxjava.myapplication.data.RetrofitUsersRepoImpl
import com.rxjava.myapplication.domain.repos.UsersRepo

//Чтобы UI не зависел от DATA введен класс Application. Не относится ни к одному из слоев

// App - глобальный SingleTone/Абстракция на все приложение. Общая точка доступа - прописана в Манифесте
// Важно, что Application наследуется от Context.
// applicationContext, который можно достать из любого context, это Application.
// В приложении можно приводиться к своему собственному App ("as App")


class App : Application() {

    //by lazy -> В момент старта не будет создаваться, эеономия ресурсов. Чтобы не загружать приложение на старте
    //Будет создан в единственном экземпляре, т.к. lazy и перезаписать нельзя, т.к. val
    val usersRepo: UsersRepo by lazy { RetrofitUsersRepoImpl() } }

// Экстеншн. Сделан синтаксический сахар. Позволяет из любого места получить App См. функцию fun extractViewModel() в Main Activity
val Context.app: App get() = applicationContext as App


//Экстеншн можно сделать для фрагмента
//val Fragment.app: App get() = requireContext().applicationContext as App