package com.rxjava.myapplication.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rxjava.myapplication.R
import com.rxjava.myapplication.databinding.ItemUserBinding
import com.rxjava.myapplication.domain.entities.UsersEntity

//в конструктор UsersViewHolder передается parent из которого нужно создать элемент
//в RecyclerView.ViewHolder должен попасть заинфлейченный элемент
//только UsersViewHolder знает, какая разметка используется

class UsersViewHolder (parent: ViewGroup, private val inItemClickListener: (usersEntity: UsersEntity) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)) {
    private lateinit var usersEntity: UsersEntity


    private val binding = ItemUserBinding.bind(itemView).apply {
        root.setOnClickListener {
            inItemClickListener.invoke(usersEntity)
        }
    }

    fun bind(usersEntity: UsersEntity) {
        this.usersEntity = usersEntity
        binding.avatarImageView.load(usersEntity.avatarUrl)      //загрузка через Coil
        binding.loginTextView.text = usersEntity.login
        binding.uidTextView.text = usersEntity.id.toString()
    }

}