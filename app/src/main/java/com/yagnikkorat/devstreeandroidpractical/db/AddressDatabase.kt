package com.yagnikkorat.devstreeandroidpractical.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yagnikkorat.devstreeandroidpractical.SaveAddressModel

@Database(entities = [SaveAddressModel::class], version = 1)
abstract class AddressDatabase : RoomDatabase() {

    abstract fun addressDao() : AddressDao

    companion object{
        @Volatile
        private var INSTANCE: AddressDatabase? = null

        fun getDatabase(context: Context): AddressDatabase {
            if (INSTANCE == null) {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context,
                        AddressDatabase::class.java,
                        "addressDB")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}