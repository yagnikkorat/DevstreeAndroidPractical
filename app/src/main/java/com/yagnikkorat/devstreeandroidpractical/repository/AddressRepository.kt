package com.yagnikkorat.devstreeandroidpractical.repository

import androidx.lifecycle.LiveData
import com.yagnikkorat.devstreeandroidpractical.SaveAddressModel
import com.yagnikkorat.devstreeandroidpractical.db.AddressDao

class AddressRepository(
    private val addressDao: AddressDao
) {

    val allAddress: LiveData<List<SaveAddressModel>> = addressDao.getAllAddress()
    val allAddressACE: LiveData<List<SaveAddressModel>> = addressDao.getAllAddressASC()
    val allAddressDECC: LiveData<List<SaveAddressModel>> = addressDao.getAllAddressDESC()


    suspend fun insert(saveAddressModel: SaveAddressModel) {
        addressDao.addAddress(saveAddressModel)
    }


    suspend fun delete(saveAddressModel: SaveAddressModel) {
        addressDao.deleteAddress(saveAddressModel)
    }

    suspend fun update(saveAddressModel: SaveAddressModel) {
        addressDao.updateAddress(saveAddressModel)
    }
}







