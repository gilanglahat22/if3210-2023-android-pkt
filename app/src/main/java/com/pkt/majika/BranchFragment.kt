package com.pkt.majika

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.pkt.majika.utils.BranchAdapter
import com.pkt.majika.utils.Config
import com.pkt.majika.utils.retrofit.DataBranch
import com.pkt.majika.utils.retrofit.EndpointBranch
import kotlinx.coroutines.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BranchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BranchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_branch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var branchList: List<DataBranch> = arrayListOf()

        val branchListRecycler: RecyclerView = view.findViewById(R.id.branchListRecyclerView)
        branchListRecycler.layoutManager = LinearLayoutManager(context)
        branchListRecycler.setHasFixedSize(true)

        val branchAdapter = BranchAdapter()
        branchAdapter.addBranches(branchList)
        branchListRecycler.adapter = branchAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val branchListRequest =
                        Config.getInstance().create(EndpointBranch::class.java).getBranch()
                    branchList = branchListRequest.body()!!.data
                    println("Request Done")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    //withContext(Dispatchers.Main) { Toast.makeText(thisContext, "connection timeout", Toast.LENGTH_SHORT).show()}
                }
            }

            withContext(Dispatchers.Main){

                println("Executing Request")

                val adapter = BranchAdapter()

                adapter.addBranches(branchList)

                branchListRecycler.swapAdapter(adapter, true)

                val branchSearchView: TextInputEditText = view.findViewById(R.id.branchSearchInput)

                branchSearchView.addTextChangedListener {
                    val newBranchList = branchList.filter { dataBranch: DataBranch ->
                        dataBranch.name.lowercase().contains(branchSearchView.text.toString().lowercase())
                    }

                    val newBranchAdapter = BranchAdapter()
                    newBranchAdapter.addBranches(newBranchList)

                    branchListRecycler.swapAdapter(newBranchAdapter, true)
                }
                println("Request Complete")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BranchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = BranchFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}