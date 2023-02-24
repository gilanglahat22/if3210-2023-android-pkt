package com.pkt.majika.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.pkt.majika.R

data class CurrentPrice(
    val currency : String,
    val totalPrice : Int,
)
class CartItemViewsHold(clickHandler: CartItemClickHandler, itemView: View) : RecyclerView.ViewHolder(itemView){
    val name : TextView = itemView.findViewById(R.id.nama_cart)
    val price : TextView = itemView.findViewById(R.id.harga_cart)
    var quantity : TextView = itemView.findViewById(R.id.quantitas_cart)
    val add : Button = itemView.findViewById(R.id.add_cart)
    val dec : Button = itemView.findViewById(R.id.decrease_cart)
    val clickHandler = clickHandler
}

class CartItemControllers(val clickHandler: CartItemClickHandler, val cartItems: ArrayList<CartItem>):
    RecyclerView.Adapter<CartItemViewsHold>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewsHold {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_list, parent, false)
        return CartItemViewsHold(clickHandler, itemView)
    }

    override fun onBindViewHolder(holder: CartItemViewsHold, position: Int) {
        val currentItem = cartItems[position]
        holder.name.text = currentItem.name
        holder.price.text = currentItem.currency + ". " + currentItem.price.toString()
        holder.quantity.text =  currentItem.quantity.toString()
        holder.add.setOnClickListener {
            holder.clickHandler.set(CartItem(currentItem.name,currentItem.price,currentItem.quantity + 1, currentItem.currency,currentItem.countSold,currentItem.description))
            notifyDataSetChanged()
        }

        holder.dec.setOnClickListener {
            if (currentItem.quantity == 1){
                holder.clickHandler.remove(CartItem(currentItem.name,currentItem.price,currentItem.quantity, currentItem.currency,currentItem.countSold,currentItem.description))
            }
            else {
                holder.clickHandler.set(CartItem(currentItem.name,currentItem.price,currentItem.quantity - 1, currentItem.currency,currentItem.countSold,currentItem.description))
            }

            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

}


@Entity(tableName = "cart_item_table", primaryKeys = ["name", "price", "currency", "countSold", "description"])
data class CartItem(
    @ColumnInfo(name = "name")  val name : String,
                                val price : Int,
                                var quantity : Int,
                                var currency : String,
                                var countSold : Int,
                                var description : String,
)

class CartItemModelFact(private val repository: CartItemResources) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartItemViewModels::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartItemViewModels(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CartItemResources(private val models: CartItemModels) {

    val allCartItems: Flow<List<CartItem>> = models.get()

    fun getAll(): List<CartItem> {
        return models.getAll()
    }

    fun insert(cartItem: CartItem){
        models.insert(cartItem)
    }

    fun remove(cartItem: CartItem){
        models.delete(cartItem)
    }

    fun update(cartItem: CartItem){
        models.update(cartItem)
    }

    fun clearAll(){
        models.deleteAll()
    }

    fun getHargaTotal(): List<CurrentPrice> {
        return models.getHargaTotal()
    }
}

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [CartItem::class], version = 1)
abstract class CartItemRoomDatabase : RoomDatabase() {

    abstract fun CartItemModels() : CartItemModels

    // Singleton prevents multiple instances of database
    companion object {
        @Volatile
        private var INSTANCE: CartItemRoomDatabase? = null

        fun getDatabase(context: Context): CartItemRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartItemRoomDatabase::class.java,
                    "majikaDB"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class CartItemViewModels(private val repository: CartItemResources) : ViewModel() {

    val allItems: LiveData<List<CartItem>> = repository.allCartItems.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */

    fun getAll(): List<CartItem> {
        return repository.getAll()
    }

    fun insert(item: CartItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun set(item: CartItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun remove(item: CartItem) = viewModelScope.launch {
        repository.remove(item)
    }

    fun clearAll() = viewModelScope.launch {
        repository.clearAll()
    }

    fun getHargaTotal(): List<CurrentPrice> {
        return repository.getHargaTotal()
    }
}

interface CartItemClickHandler {
    fun insert(item: CartItem)
    fun remove(item: CartItem)
    fun set(item: CartItem)
}

@Dao
interface CartItemModels {

    @Query("SELECT * FROM cart_item_table")
    fun get(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_item_table WHERE name = :name")
    fun getByName(name: String): CartItem

    @Query("SELECT currency, sum(quantity*price) AS totalPrice FROM cart_item_table GROUP BY currency")
    fun getHargaTotal(): List<CurrentPrice>

    @Query("SELECT * FROM cart_item_table")
    fun getAll(): List<CartItem>

    @Update
    fun update(cartItem: CartItem)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(cartItem: CartItem)

    @Delete
    fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_item_table")
    fun deleteAll()
}