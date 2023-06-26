package com.yagnikkorat.devstreeandroidpractical

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "saveAddress")
data class SaveAddressModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    val name: String? = null,
    val address: String? = null,
    val distance: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Serializable