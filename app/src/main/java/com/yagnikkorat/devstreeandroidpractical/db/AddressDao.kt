package com.yagnikkorat.devstreeandroidpractical.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yagnikkorat.devstreeandroidpractical.SaveAddressModel

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAddress(quotes: SaveAddressModel)

    @Delete
    suspend fun deleteAddress(note: SaveAddressModel)

    @Update
    suspend fun updateAddress(note: SaveAddressModel)

    @Query("SELECT * FROM saveAddress")
    fun getAllAddress(): LiveData<List<SaveAddressModel>>

    @Query("Select * from saveAddress order by distance ASC")
    fun getAllAddressASC(): LiveData<List<SaveAddressModel>>

    @Query("Select * from saveAddress order by distance DESC")
    fun getAllAddressDESC(): LiveData<List<SaveAddressModel>>


}