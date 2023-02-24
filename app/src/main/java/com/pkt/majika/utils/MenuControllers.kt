package com.pkt.majika.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pkt.majika.R

class MenuDataView(itemView: View, clickHandler: CartItemClickHandler): RecyclerView.ViewHolder(itemView){
    val name : TextView = itemView.findViewById(R.id.food_name)
    val description : TextView = itemView.findViewById(R.id.food_description)
    var quantity : TextView = itemView.findViewById(R.id.menu_quantity)
    val price : TextView = itemView.findViewById(R.id.food_price)
    val sold : TextView = itemView.findViewById(R.id.count_sold)
    val add : Button = itemView.findViewById(R.id.add_menu)
    val dec : Button = itemView.findViewById(R.id.decrease_menu)
    val clickHandler = clickHandler
}

class MenuControllers(val clickHandler : CartItemClickHandler, val listMenu: ArrayList<CartItem>) :
    RecyclerView.Adapter<MenuDataView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuDataView {
        return MenuDataView(LayoutInflater.from(parent.context).inflate(R.layout.row_menu, parent, false), clickHandler)
    }

    override fun onBindViewHolder(menuDataView: MenuDataView, position: Int) {
        val currentItem = listMenu[position]
        fun initilizeHolder(){
            menuDataView.name.text = currentItem.name
            menuDataView.price.text = currentItem.currency + ". " + currentItem.price
            menuDataView.sold.text = "Sold " + currentItem.countSold.toString()
            menuDataView.description.text = currentItem.description
            menuDataView.quantity.text = currentItem.quantity.toString()
        }
        fun updateCurItem(){
            menuDataView.clickHandler.set(CartItem(currentItem.name,currentItem.price.toInt(),currentItem.quantity + 1,
                currentItem.currency, currentItem.countSold,currentItem.description))
        }

        fun addCurItem(){
            menuDataView.clickHandler.insert(CartItem(currentItem.name,currentItem.price.toInt(),1,
                currentItem.currency,currentItem.countSold,currentItem.description))
        }

        fun updateDec_CurItem(){
            menuDataView.clickHandler.set(CartItem(currentItem.name,currentItem.price.toInt(),currentItem.quantity - 1,
                currentItem.currency,currentItem.countSold,currentItem.description))
        }

        fun removeCurItem(){
            menuDataView.clickHandler.remove(CartItem(currentItem.name,currentItem.price.toInt(),1,
                currentItem.currency,currentItem.countSold,currentItem.description))
        }

        initilizeHolder()

        menuDataView.add.setOnClickListener {
            if(currentItem.quantity>0){
                updateCurItem()
            }
            else{
                addCurItem()
            }
            listMenu[position].quantity++
            notifyDataSetChanged()
        }

        if (currentItem.quantity>0){
            menuDataView.dec.setVisibility(View.VISIBLE)

        } else{
            menuDataView.dec.setVisibility(View.GONE)
        }

        menuDataView.dec.setOnClickListener{
            if(listMenu[position].quantity > 1){
                updateDec_CurItem()
            }else if(listMenu[position].quantity == 1){
                removeCurItem()
            }
            if(listMenu[position].quantity >= 1) {
                listMenu[position].quantity--
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return listMenu.size
    }
}