package com.example.matheusmaxwellmeireles.borrowme

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail_item.*
import java.io.ByteArrayInputStream

class ActivityDetailItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        val idBorrow = intent.getIntExtra("idBorrow", -1)
        val listItem = MyApplication.database!!.ItemDAO().getAllItems()
        val listPerson = MyApplication.database!!.PersonDAO().getAllPerson()
        val listBorrow = MyApplication.database!!.BorrowDAO().getAllBorrow()
        var idItem: Int? = 0
        var idPerson: Int? = 0
        var dateBorrow: String? = null
        var dateReturn: String? = null

        if(idBorrow != -1){
            for (i in listBorrow){
                if(i.id == idBorrow){
                    idItem = i.idItem
                    idPerson = i.idPerson
                    dateBorrow = i.borrowDate
                    dateReturn = i.returnDate
                }
            }
            txtBorrowDate.setText(dateBorrow)
            txtReturnDate.setText(dateReturn)
            for (i in listItem){
                if(i.idItem == idItem){
                    tvNameItemDetail.setText(i.nome)
                    val arrayInputStream = ByteArrayInputStream(i.image)
                    val bitmapProfile = BitmapFactory.decodeStream(arrayInputStream)
                    imageItemDetail.setImageBitmap(bitmapProfile)
                }
            }
            for (i in listPerson){
                if(i.idPerson == idPerson){
                    txtNamePerson.setText(i.name)
                }
            }

        }
        else{
            Toast.makeText(this, "Ops, some error occurred.", Toast.LENGTH_SHORT).show()
        }

        btnVoltarList.setOnClickListener {
           onBackPressed()
        }

        txtNamePerson.setOnClickListener {
            val intent = Intent(this, ActivityPersonDetail::class.java)
            intent.putExtra("idPerson", idPerson)
            startActivity(intent)
        }

    }


}
