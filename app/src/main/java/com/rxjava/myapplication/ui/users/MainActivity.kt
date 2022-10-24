package com.rxjava.myapplication.ui.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.rxjava.myapplication.app
import com.rxjava.myapplication.databinding.ActivityMainBinding
import com.rxjava.myapplication.domain.entities.UsersEntity
import com.rxjava.myapplication.ui.profile.ProfileActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //сообщить ViewModel, что был нажат элемент списка и в метод onUserClick(it) передать того юзера, на которого нажали (it)
    private val adapter = UsersAdapter {
        viewModel.onUserClick(it)
    }

    //вместо Presenter - ViewModel, attach() и detach() не нужны. LiveData умеет отпысываться, т.к. она подключена к жизненному циклу. Без LiveData в MVVM нужно вручную отписыватся
    private lateinit var viewModel: UsersContract.ViewModel

    //Чтобы отдельно не заводить переменные для отписки всех ViewModel, можно разово завести одну на всех (для rxjava)
    private val viewModelDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        /* 2022.10.20 работа с LiveData
        //изменяются значения ViewModel (interface в UsersContract). Нужно (и можно) только подписаться на ViewModel'и, т.к. тип LiveData
        // через observe() подписка на переменные. Переменные измменятся (progressLiveData и т.д.) -> вызовутся функции showProgress/showUsers и т.д.
        // ViewModel вызывает фукнции/сэтить значения в LiveData (а не напрямую в View)
        // технически ссылка у ViewModel на MainActivity есть (this). Некорректно говорить, что ViewModel отвязалась от View (Activity) и больше ничего о ней формально не знает...
        // ...знание как о классов друг о друге пропало, но хранение ссылки осталось. Связанность все-равно есть

        // После поворота экрана подписываюсь на errorLiveData заново, заново создается observer (метод observe() в SingleEventLiveData

        viewModel.progressLiveData.observe(this) { showProgress(it) }
        viewModel.usersLiveData.observe(this) { showUsers(it) }
        viewModel.errorLiveData.observe(this) { showError(it) }
        viewModel.openProfileLiveData.observe(this) { openProfileScreen() } //зажгли событие (переход на новый экран) в UsersViewModel,а в Activity его нужно поймать.
        // Но в названии LiveDat'ы лучше не писать openScreen т.к. как именно будет выглядеть информация Активити не должна знать
         */

        viewModel = extractViewModel()

        // 2022.10.24 Замена LiveData на Observable. Концептуально - одно и то же,
        // разница в том, что создаются цепочки сбытий и они хранится в переменной "val viewModelDisposable", теперь можно отписаться в onDestroy() от всех одновременно

        viewModelDisposable.addAll(
            viewModel.progressLiveData.subscribe{ showProgress(it) },
                    viewModel.usersLiveData.subscribe{ showUsers(it) }, //каждая сточка - это выражение, которое возвращает значение цепочки
                    viewModel.errorLiveData.subscribe{ showError(it) },
                    viewModel.openProfileLiveData.subscribe{ openProfileScreen() }
        )

    }

    // Решается проблема жизненного цикла (например при повороте экрана происходит отписка от ВСЕХ подписок).
    override fun onDestroy() {
        viewModelDisposable.dispose()
        super.onDestroy()
    }



    //правильно Intent создавать  в Activity, а не в ViewModel
    private fun openProfileScreen() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun extractViewModel(): UsersContract.ViewModel {
        return lastCustomNonConfigurationInstance as? UsersContract.ViewModel
            ?: UsersViewModel(app.usersRepo)        //т.к. Activity это Context к Context добавлено разрешение "app", получил доступ к SingleTone (класс App). Вместо "applicationContext as App"
    }

    //метод срабатывает при повороте экрана/пересоздании активити, когда уничтожается активити и пересоздается заново. В этом случае запоминается ссылка на хранилище ViewModel'ей
    //сохранение состояния ViewModel, позволяет пережить объектам (сссылкам на них) поворот активити
    override fun onRetainCustomNonConfigurationInstance(): UsersContract.ViewModel {
        return viewModel
    }

    private fun initViews() {
        binding.refreshButton.setOnClickListener {
            viewModel.onRefresh()
        }
        initRecyclerView()

        showProgress(false)
    }


    // вместо override -> private
    private fun showUsers(users: List<UsersEntity>) {
        adapter.setData(users)
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(inProgress: Boolean) {
        binding.progressBar.isVisible = inProgress
        binding.usersRecyclerView.isVisible = !inProgress
    }

    private fun initRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = adapter
    }
}