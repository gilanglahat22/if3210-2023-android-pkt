package com.pkt.majika

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pkt.majika.databinding.ActivityMainBinding
import com.pkt.majika.utils.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartFragment : Fragment(), CartItemClickHandler {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemList: ArrayList<CartItem>
    private lateinit var bind: ActivityMainBinding
    private lateinit var totalPrice : TextView
    private lateinit var btnPembayaran : Button

    private val cartItemViewModel: CartItemViewModels by viewModels {
        CartItemModelFact((activity?.application as App).repository)
    }

    private fun initializeCartGetter(view: View){
        totalPrice = view.findViewById(R.id.harga_total)
        cartItemList = arrayListOf<CartItem>()
    }

    private fun getRecycleView(view: View){
        recyclerView = view.findViewById(R.id.Daftar_Cart)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = CartItemControllers(this,cartItemList)
    }

    private fun paymentView(view: View){
        btnPembayaran = view.findViewById<Button>(R.id.buttonPembayaran)
        btnPembayaran.setOnClickListener {
            val intent = Intent(activity, PaymentFragment::class.java)
            intent.putExtra("totalPrice", getPrice())
            startActivity(intent)
        }
    }

    private fun getPrice(): String{
        var temp = ""
        val totalPriceTemp = cartItemViewModel.getHargaTotal()
        fun getStringTransaction(){
            for (i in 0 until totalPriceTemp.size){
                temp +=  totalPriceTemp[i].currency + ". " + totalPriceTemp[i].totalPrice
                if (i != totalPriceTemp.size -1){
                    temp += " + "
                }
            }
        }
        getStringTransaction()
        if (temp != "") {
            btnPembayaran.setVisibility(View.VISIBLE)
            return temp
        }
        else {
            btnPembayaran.setVisibility(View.GONE)
            return "IDR. 0"
        }
    }

    private fun showAllCartItem(){
        cartItemViewModel.allItems.observe(viewLifecycleOwner){
            recyclerView.adapter = CartItemControllers(this,ArrayList(it))
            totalPrice.text = this.getPrice()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        this.paymentView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initializeCartGetter(view)

        this.getRecycleView(view)
        totalPrice.text = this.getPrice()
        this.showAllCartItem()
    }

    override fun insert(cartItem: CartItem){
        cartItemViewModel.insert(cartItem)
    }

    override fun remove(cartItem: CartItem){
        cartItemViewModel.remove(cartItem)
    }

    override fun set(cartItem: CartItem){
        cartItemViewModel.set(cartItem)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}