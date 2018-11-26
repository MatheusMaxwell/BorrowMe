package com.example.matheusmaxwellmeireles.borrowme

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream


class MyAdapter(private val context: Context, private val borrows: List<Entities.Borrow>,
                private val items: List<Entities.Item>, private val persons: List<Entities.Person>) : BaseAdapter() {

    override fun getCount(): Int {
        return borrows.size
    }

    override fun getItem(i: Int): Any {
        return borrows[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        var view = view

        val borrow = borrows[i]
        var item: Entities.Item? = null
        for (i in items){
            if(i.idItem == borrow.idItem){
                item = i
            }
        }
        var person: Entities.Person? = null
        for(i in persons){
            if(i.idPerson == borrow.idPerson){
                person = i
            }
        }

        if (view == null) {
            view = View.inflate(context, R.layout.layout_listview_history, null)
        }

        val image = view!!.findViewById<ImageView>(R.id.imageView)
        val name = view!!.findViewById<TextView>(R.id.tvNameItem)
        val dateBorrow = view!!.findViewById<TextView>(R.id.tvBorrowDateItem)

        name.setText(item!!.nome)
        dateBorrow.setText(borrow.returnDate)

        val arrayInputStream = ByteArrayInputStream(item.image)
        val bitmapProfile = BitmapFactory.decodeStream(arrayInputStream)
        image.setImageBitmap(bitmapProfile)

        //image.setImageBitmap(item.image as Bitmap)


        return view
    }

}
