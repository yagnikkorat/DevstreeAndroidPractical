package com.yagnikkorat.devstreeandroidpractical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yagnikkorat.devstreeandroidpractical.databinding.ItemLocationBinding

class AddressAdapter(
    private val addressClickDeleteInterface: AddressClickDeleteInterface,
    private val addressClickEditInterface: AddressClickEditInterface
) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private val allAddress = ArrayList<SaveAddressModel>()

    inner class ViewHolder(val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(binding) {
                with(allAddress[position]) {
                    txtName.text = name
                    txtAddress.text = address

                    if (distance > 0.0) {
                        txtDistance.text = "Distance : $distance Km"
                        txtDistance.visibility = View.VISIBLE
                    } else {
                        txtDistance.text = ""
                        txtDistance.visibility = View.GONE
                    }
                }
            }
        }

        holder.binding.imgDelete.setOnClickListener {
            addressClickDeleteInterface.onDeleteIconClick(allAddress[position])
        }

        holder.binding.imgEdit.setOnClickListener {
            addressClickEditInterface.onEditIconClick(allAddress[position])
        }
    }

    override fun getItemCount(): Int {
        return allAddress.size
    }

    fun updateList(newList: List<SaveAddressModel>) {
        allAddress.clear()
        allAddress.addAll(newList)
        notifyDataSetChanged()
    }
}

interface AddressClickDeleteInterface {
    fun onDeleteIconClick(address: SaveAddressModel)
}

interface AddressClickEditInterface {
    fun onEditIconClick(address: SaveAddressModel)
}
