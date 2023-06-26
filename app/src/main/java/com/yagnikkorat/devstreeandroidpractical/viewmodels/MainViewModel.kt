package com.yagnikkorat.devstreeandroidpractical.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.yagnikkorat.devstreeandroidpractical.SaveAddressModel
import com.yagnikkorat.devstreeandroidpractical.db.AddressDatabase
import com.yagnikkorat.devstreeandroidpractical.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AddressRepository

    private val addressAscending: LiveData<List<SaveAddressModel>>
    private val addressDescending: LiveData<List<SaveAddressModel>>
    private val addressNormal: LiveData<List<SaveAddressModel>>

    val address = MediatorLiveData<List<SaveAddressModel>>()
    private var currentOrder = AddressOrder.Normal

    enum class AddressOrder { Ascending, Descending, Normal, }

    init {
        val dao = AddressDatabase.getDatabase(application).addressDao()
        repository = AddressRepository(dao)
        addressAscending = repository.allAddressACE
        addressDescending = repository.allAddressDECC
        addressNormal = repository.allAddress

        address.addSource(addressNormal) { result ->
            if (currentOrder == AddressOrder.Normal) {
                result?.let { address.value = it }
            }
        }

        address.addSource(addressAscending) { result ->
            if (currentOrder == AddressOrder.Ascending) {
                result?.let { address.value = it }
            }
        }
        address.addSource(addressDescending) { result ->
            if (currentOrder == AddressOrder.Descending) {
                result?.let { address.value = it }
            }
        }
    }

    fun deleteAddress(saveAddressModel: SaveAddressModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(saveAddressModel)
    }

    fun updateAddress(saveAddressModel: SaveAddressModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(saveAddressModel)
    }


    fun addAddress(saveAddressModel: SaveAddressModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(saveAddressModel)
    }

    fun rearrangeAddress(order: AddressOrder) = when (order) {
        AddressOrder.Ascending -> addressAscending.value?.let { address.value = it }
        AddressOrder.Descending -> addressDescending.value?.let { address.value = it }
        AddressOrder.Normal -> addressNormal.value?.let { address.value = it }
    }.also { currentOrder = order }
}