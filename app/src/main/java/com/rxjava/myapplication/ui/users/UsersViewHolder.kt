package com.rxjava.myapplication.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rxjava.myapplication.R
import com.rxjava.myapplication.databinding.ItemUserBinding
import com.rxjava.myapplication.domain.entities.UserEntity

//в конструктор UsersViewHolder передается parent из которого нужно создать элемент
//в RecyclerView.ViewHolder должен попасть заинфлейченный элемент
//только UsersViewHolder знает, какая разметка используется

class UsersViewHolder (parent: ViewGroup, private val inItemClickListener: (userEntity: UserEntity) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)) {
    private lateinit var userEntity: UserEntity


    private val binding = ItemUserBinding.bind(itemView).apply {
        root.setOnClickListener {
            inItemClickListener.invoke(userEntity)
        }
    }

    fun bind(userEntity: UserEntity) {
        this.userEntity = userEntity
        binding.avatarImageView.load(userEntity.avatarUrl)      //загрузка через Coil
        binding.loginTextView.text = userEntity.login
        binding.uidTextView.text = userEntity.id.toString()
    }

}