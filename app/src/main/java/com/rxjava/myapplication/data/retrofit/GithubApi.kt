package com.rxjava.myapplication.data.retrofit

import com.rxjava.myapplication.domain.entities.UsersEntity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET

// СЛОЙ ДАННЫХ. Зависит от DOMAIN (ДАННЫХ) UI

// БАЗОВЫЙ Принцип работы rx java в данном примере -> в rx java есть объект "Observable" (наблюдаемый), который испускает событие (за нас это делает retrofit) и есть observer (наблюдатели),...
// ...которые будут эти собитыя потреблять и смотреть, что произошло

// 2022.10.24 c rx java
// заменяем Call<List<...>> на Single<List<...>> (вместо возвращения Пользователя, возвращаем Single)
interface GithubApi {
    @GET("users")
    fun getUsers(): Single<List<UsersEntity>>
}

/* 2022.10.20 без rx java
interface GithubApi {
    @GET("users")
    fun getUsers(): Call<List<UsersEntity>>     // для перехода на rx java заменяем Call<List<...>> на Single<List<...>> (вместо возвращения Пользователя, возвращаем Single)
}
 */

/* Теория по RxJava
 Существуют разные объекты они испускают события на которые можно подписаться при момощи метода subscribe() и обрабатывать случаи успеха и ошибок:
 Observable - события приходят регулярно (объект через Observable испускает данные постоянно)
 Single - событие, которое придет только один раз и закрывается (объект через Single испускает событие только один раз и все)
 Maybe - событие может прийти, а может не прийти может закрывается
 Completable (без аргументов) - когда не нужен результат. Ничего не испускает только закрывается (завершает свое существование)...
 ...Когда нужно передать свои данные просто закрыться и все. Например передача данных профиля пользователя на сервер. Запрос закончился и всё

 События бывают нескольких типов:
 onComplete - цепочка завершилась и больше событий НЕ будет. Например отправляют // событий и в конце complete -> цепочка бесполезна и данные через нее НЕ придут
 Через Observable присылается неограниченное кол-во элементов а потом complete или нет
 Через Single точно приходит ОДНО значение и сразу complete
 Через Maybe может придет значение или просто complete
 Через Completable ничего не произойдет только complete
 */