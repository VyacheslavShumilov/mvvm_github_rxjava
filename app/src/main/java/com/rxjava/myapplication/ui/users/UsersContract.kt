package com.rxjava.myapplication.ui.users

import androidx.lifecycle.LiveData
import com.rxjava.myapplication.domain.entities.UsersEntity
import io.reactivex.rxjava3.core.Observable

interface UsersContract {
    interface ViewModel {

        // 2022.10.24 c RxJava
        // Замена LiveData на Observable -> см. MainActivity

        val usersLiveData: Observable<List<UsersEntity>>
        val errorLiveData: Observable<Throwable>
        val progressLiveData: Observable<Boolean>
        val openProfileLiveData: Observable<Unit> // для открытия новой активити

        /* 2022.10.20 без RxJava
        // View сама подписывается на ViewModel и ждет когда у нее изменится значение
        // Если наружу из ViewModel можно отдать только LiveData (неизменяемую - нельзя установить значение postValue() или setValue(), только получить). Иначе (если MutableLiveData) View может постить в ViewModel - реализует логику - нельзя!
        // у MutableLiveData в отличие от LiveData методы открыты наружу postValue() и setValue(). У LiveData эти методы protected

        // LiveData получает значение и хранит его -> восстановленние данных после поворота экрана.
        // Но в ViewModel нет ссылки на View -> вопрос с передачей во View одноразового события, кторое не нужно восстанавливать при повороте экрана
        // Для решения этой проблемы создается класс SingleEventLiveData

        val usersLiveData: LiveData<List<UsersEntity>>
        val errorLiveData: LiveData<Throwable>
        val progressLiveData: LiveData<Boolean>
        val openProfileLiveData: LiveData<Unit> // для открытия новой активити
         */

        fun onRefresh()     //управляющий метод
        fun onUserClick(usersEntity: UsersEntity)   // обработка нажатия на юзера в списке
    }

}