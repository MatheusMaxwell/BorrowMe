package com.example.matheusmaxwellmeireles.borrowme

import android.arch.persistence.room.*

@Dao
interface ItemDAO {

    @Query("SELECT * FROM item")
    fun getAllItems(): List<Entities.Item>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(vararg users: Entities.Item)

    @Update
    fun updateItem(user: Entities.Item)

    @Delete
    fun deleteItem(user: Entities.Item)

}