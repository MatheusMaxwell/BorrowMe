package com.example.matheusmaxwellmeireles.borrowme

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(version = 1, entities = arrayOf(Entities.Person::class, Entities.Item::class, Entities.Borrow::class))
abstract class AppDataBase : RoomDatabase(){
    abstract fun PersonDAO(): PersonDAO
    abstract fun ItemDAO(): ItemDAO
    abstract fun BorrowDAO(): BorrowDAO
}

