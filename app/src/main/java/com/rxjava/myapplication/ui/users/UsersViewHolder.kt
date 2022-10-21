package com.rxjava.myapplication.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rxjava.myapplication.R
import com.rxjava.myapplication.databinding.ItemUserBinding
import com.rxjava.myapplication.domain.entities.UsersEntity
// Holder - самостоятельная единица логическая и визуальная. Не зависит от Адаптера
// в конструктор UsersViewHolder передается parent из которого нужно создать элемент
// в RecyclerView.ViewHolder должен попасть заинфлейченный элемент
// только UsersViewHolder знает, какая разметка используется


class UsersViewHolder (parent: ViewGroup, private val onItemClickListener: (usersEntity: UsersEntity) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)) {
    private lateinit var usersEntity: UsersEntity


    // 1) при нажатии на элемент у него вызывается onItemClickListener (см. конструктор UsersViewHolder)
    private val binding = ItemUserBinding.bind(itemView).apply {
        //вместо root можно сделать клик по аватарке "avatarImageView.setOn..."
        avatarImageView.setOnClickListener {
            //вызываю callback который передан в конструкторе при создании UsersViewHolder
            onItemClickListener.invoke(usersEntity) // 3) воспользовался объектом при нажатии
        }
    }

    fun bind(usersEntity: UsersEntity) {
        this.usersEntity = usersEntity // 2) сохранение дополнительного элемента, который нажимается
        binding.avatarImageView.load(usersEntity.avatarUrl)      //загрузка через Coil
        binding.loginTextView.text = usersEntity.login
        binding.uidTextView.text = usersEntity.id.toString()
    }
}