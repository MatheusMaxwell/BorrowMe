package com.example.matheusmaxwellmeireles.borrowme

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import android.content.DialogInterface
import android.icu.util.TimeUnit
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_new.*

class MainActivity : AppCompatActivity() {

    val historyFragment = HistoryFragment()
    val newFragment = NewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFragment(newFragment)
        nav.selectedItemId = R.id.itemNew

        nav.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){

                R.id.itemHistory->
                    setFragment(historyFragment)


                R.id.itemNew->
                    setFragment(newFragment)



            }
            true

        }


    }

    private fun setFragment(fragment: Fragment){

        val fragmenteTransation = supportFragmentManager.beginTransaction()
        fragmenteTransation.replace(R.id.frame, fragment)
        fragmenteTransation.commit()

    }
}
