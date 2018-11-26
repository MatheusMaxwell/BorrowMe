package com.example.matheusmaxwellmeireles.borrowme

import android.app.Application
import android.arch.persistence.room.Room

open class MyApplication : Application() {

    companion object {
        var database: AppDataBase? = null
    }

    override fun onCreate() {
        super.onCreate()
        //Room
        database = Room.databaseBuilder(this, AppDataBase::class.java, "borrowme").allowMainThreadQueries().build()
    }

}