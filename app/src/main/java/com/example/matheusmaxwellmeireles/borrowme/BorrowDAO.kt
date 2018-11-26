package com.example.matheusmaxwellmeireles.borrowme

import android.arch.persistence.room.*

@Dao
interface BorrowDAO {

    @Query("SELECT * FROM borrow")
    fun getAllBorrow(): List<Entities.Borrow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBorrow(vararg users: Entities.Borrow)

    @Update
    fun updateBorrow(user: Entities.Borrow)

    @Delete
    fun deleteBorrow(user: Entities.Borrow)

}