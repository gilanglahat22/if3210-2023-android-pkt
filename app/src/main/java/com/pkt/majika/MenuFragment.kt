package com.pkt.majika

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.pkt.majika.databinding.ActivityMainBinding
import com.pkt.majika.utils.*
import retrofit2.Response
import com.pkt.majika.utils.retrofit.DatasMenu
import com.pkt.majika.utils.retrofit.EndpointMenu
import kotlinx.coroutines.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment(), CartItemClickHandler{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bind: ActivityMainBinding
    private lateinit var tempAllCart: List<CartItem>
    private lateinit var thisContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getAllItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var foodModels = arrayListOf<CartItem>()
        var drinkModels = arrayListOf<CartItem>()
        var tempFoodModels = arrayListOf<CartItem>()
        var tempDrinkModels = arrayListOf<CartItem>()
        val contxt = this
        lateinit var recyclerDrink: RecyclerView
        lateinit var recyclerFood: RecyclerView
        lateinit var adapterDrink: MenuControllers
        lateinit var adapterFood: MenuControllers
        lateinit var input_text: TextInputEditText

        fun getAllMenu(allMenu: Response<DatasMenu>) {
            val dataMenu = allMenu.body()!!.data
            for(m in dataMenu){
                var quantity = 0
                for(n in tempAllCart){
                    if(m.name == n.name && m.price.toInt() == n.price
                        && m.currency == n.currency && n.countSold == m.sold
                        && m.description == n.description){
                        quantity = n.quantity
                        break
                    }
                }
                if(m.type=="Drink") drinkModels.add(CartItem(m.name, m.price.toInt(), quantity,
                    m.currency, m.sold, m.description))
                else foodModels.add(CartItem(m.name, m.price.toInt(), quantity,
                    m.currency, m.sold, m.description))
            }
            tempFoodModels.addAll(foodModels)
            tempDrinkModels.addAll(drinkModels)
        }

        fun initializeRecycles(){
            val layoutDrink = LinearLayoutManager(context)
            recyclerDrink = view.findViewById(R.id.list_drinks)
            recyclerDrink.layoutManager = layoutDrink
            recyclerDrink.setHasFixedSize(true)
            adapterDrink = MenuControllers(contxt, tempDrinkModels)
            recyclerDrink.adapter = adapterDrink
            val layoutManager = LinearLayoutManager(context)
            recyclerFood = view.findViewById(R.id.Daftar_Makanan)
            recyclerFood.layoutManager = layoutManager
            recyclerDrink.setHasFixedSize(true)
            adapterFood = MenuControllers(contxt, tempFoodModels)
            recyclerFood.adapter = adapterFood
            recyclerFood.setNestedScrollingEnabled(false);
            recyclerDrink.setNestedScrollingEnabled(false);
            input_text = view.findViewById(R.id.text_input)
        }

        fun searchMenu(text: String){
            for (i in drinkModels.indices){
                if (drinkModels[i].name.lowercase(Locale.getDefault()).contains(text)){
                    tempDrinkModels.add(drinkModels[i])
                }
                recyclerDrink.adapter!!.notifyDataSetChanged()
            }
            for (i in foodModels.indices){
                if (foodModels[i].name.lowercase(Locale.getDefault()).contains(text)){
                    tempFoodModels.add(foodModels[i])
                }
                recyclerFood.adapter!!.notifyDataSetChanged()
            }
        }

        fun resetTemp(){
            tempFoodModels.clear()
            tempFoodModels.addAll(foodModels)
            tempDrinkModels.clear()
            tempDrinkModels.addAll(drinkModels)
            recyclerFood.adapter!!.notifyDataSetChanged()
            recyclerDrink.adapter!!.notifyDataSetChanged()
        }

        lifecycleScope.launch{
            val operate = GlobalScope.async(Dispatchers.Default){
                try{
                    val menuAllData = Config.getInstance().create(EndpointMenu::class.java).getMenu()
                    getAllMenu(menuAllData)
                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                    withContext(Dispatchers.Main) { Toast.makeText(thisContext, "connection timeout", Toast.LENGTH_SHORT).show()}
                }
            }

            operate.await()

            initializeRecycles()

            input_text.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    tempFoodModels.clear()
                    tempDrinkModels.clear()
                    val text = s.toString()!!.lowercase(Locale.getDefault())
                    if(!text.isNotEmpty()){
                        resetTemp()
                    }else{
                        searchMenu(text)
                    }
                }
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }
            })
        }
    }

    private fun getAllItem(){
        this.bind = ActivityMainBinding.inflate(layoutInflater)
        tempAllCart = cartViewModel.getAll()
    }

    private val cartViewModel: CartItemViewModels by viewModels{
        CartItemModelFact((activity?.application as App).repository)
    }

    override fun insert(item: CartItem){
        cartViewModel.insert(item)
    }

    override fun set(item: CartItem){
        cartViewModel.set(item)
    }

    override fun remove(item: CartItem){
        cartViewModel.remove(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
