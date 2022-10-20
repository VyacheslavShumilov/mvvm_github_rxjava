package com.rxjava.myapplication.ui.users

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rxjava.myapplication.domain.entities.UsersEntity

class UsersAdapter(
    private val onItemClickListener: (UsersEntity) -> Unit
) : RecyclerView.Adapter<UsersViewHolder>() {

    private val data = mutableListOf<UsersEntity>()

    //setHasStableIds и getItemId автоматически сравнивает объекты equals содержимого, при этом различает объекты по ID
    init { setHasStableIds(true) }  //у объектов почти всегда есть ID по которым их можно отличать
    override fun getItemId(position: Int) = getItem(position).id


    //создание ViewHolder сколько видно на экране + еще несколько
    //в данном случае применен принцип разнесения логики (Наполнение Item перенесено из адаптера в отдельный класс UsersViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UsersViewHolder(parent, onItemClickListener)

    //свзяь ViewHolder с данными
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //кол-во для отрисовки
    override fun getItemCount() = data.size

    //можно добавить фунцию - возвращение элемента по позиции
    private fun getItem(pos: Int) = data[pos]


    //сообщить в адаптер о имеющихся данных
    @SuppressLint("NotifyDataSetChanged")
    fun setData(users: List<UsersEntity>) {
        data.clear()            //предыдущие данные исключаются
        data.addAll(users)      //добавление новых данных
        notifyDataSetChanged()  //уведомление о изменении данных
    }

}