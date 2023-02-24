package com.pkt.majika.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.pkt.majika.R
import com.pkt.majika.utils.retrofit.DataBranch

class BranchAdapter: RecyclerView.Adapter<BranchAdapter.BranchDataView>() {

    private val branchSortedList: SortedList<DataBranch> = SortedList(DataBranch::class.java, object: SortedListAdapterCallback<DataBranch>(this) {
        override fun compare(o1: DataBranch?, o2: DataBranch?): Int {
            if (o1 != null) {
                if (o2 != null) {
                    return o1.name.compareTo(o2.name)
                }
            }
            return -1
        }

        override fun areContentsTheSame(oldItem: DataBranch?, newItem: DataBranch?): Boolean {
            return true
        }

        override fun areItemsTheSame(item1: DataBranch?, item2: DataBranch?): Boolean {
            if (item1 != null) {
                if (item2 != null) {
                    return item1.name == item2.name
                }
            }
            return false
        }
    })


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchDataView {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.branch_item, parent, false)
        return BranchDataView(view)
    }

    override fun getItemCount(): Int {
        return branchSortedList.size()
    }

    override fun onBindViewHolder(holder: BranchDataView, position: Int) {
        holder.setBranch(branchSortedList.get(position))
    }

    fun addBranches(branches: List<DataBranch>){
        branchSortedList.addAll(branches)
    }

    class BranchDataView(branchView: View): RecyclerView.ViewHolder(branchView) {
        private val branchName: TextView = branchView.findViewById(R.id.item_branch_name)
        private val branchAddress: TextView = branchView.findViewById(R.id.item_branch_address)
        private val branchPhone: TextView = branchView.findViewById(R.id.item_branch_phone)
        private val branchMapsButton: Button = branchView.findViewById(R.id.item_branch_button)
        val context: Context = branchView.context

        fun setBranch(branch: DataBranch) {
            branchName.text = branch.name
            branchAddress.text = branch.address
            branchPhone.text = branch.phone_number
            branchMapsButton.setOnClickListener {
                val intentString = "geo:0,0?q=%f,%f(%s)".format(branch.latitude, branch.longitude, branch.address)
                println(intentString)
                val gmIntentUri = Uri.parse(intentString)
                val googleMapsIntent = Intent(Intent.ACTION_VIEW, gmIntentUri)
                googleMapsIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(googleMapsIntent)
            }
        }
    }
}