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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = UsersAdapter {
        viewModel.onUserClick(it)
    }

    //вместо Presenter - ViewModel, attach() и detach() не нужны. LiveData умеет отпысываться, т.к. она подключена к жизненному циклу. Без LiveData в MVVM нужно вручную отписыватся

    private lateinit var viewModel: UsersContract.ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initViewModel()
    }

    //изменяются значения ViewModel (interface в UsersContract). Нужно (и можно) только подписаться на ViewModel'и, т.к. тип LiveData
    private fun initViewModel() {
        viewModel = extractViewModel()

        // через observe() подписка на переменные. Переменные измменятся (progressLiveData и т.д.) -> вызовутся функции showProgress/showUsers и т.д.
        // ViewModel вызывает фукнции/сэтить значения в LiveData (а не напрямую в View)
        // технически ссылка у ViewModel на MainActivity есть (this). Некорректно говорить, что ViewModel отвязалась от View (Activity) и больше ничего о ней формально не знает...
        // ...знание как о классов друг о друге пропало, но хранение ссылки осталось. Связанность все-равно есть

        // После поворота экрана подписываюсь на errorLiveData заново, заново создается observer (метод observe() в SingleEventLiveData

        viewModel.progressLiveData.observe(this) { showProgress(it) }
        viewModel.usersLiveData.observe(this) { showUsers(it) }
        viewModel.errorLiveData.observe(this) { showError(it) }
        viewModel.openProfileLiveData.observe(this) { openProfileScreen() }
    }

    private fun openProfileScreen() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun extractViewModel(): UsersContract.ViewModel {
        return lastCustomNonConfigurationInstance as? UsersContract.ViewModel
            ?: UsersViewModel(app.usersRepo)        //т.к. Activity это Context к Context доавлено разрешение "app", получил доступ к SingleTone (класс App). Вместо "applicationContext as App"
    }

    //сохранение состояния ViewModel, чпозволяет пережить объектам (сссылкам на них) поворот активити
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