package com.example.matheusmaxwellmeireles.borrowme

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.media.Image
import android.support.annotation.NonNull
import android.view.ViewDebug
import java.util.*

class Entities {

    @Entity(tableName = "item")
    data class Item(
        @ColumnInfo(name="name")
        var nome: String?,
        @ColumnInfo(name="image")
        var image: ByteArray?

    ){
        @NonNull
        @ColumnInfo(name="idItem")
        @PrimaryKey(autoGenerate = true)
        var idItem : Int = 0
        override fun toString(): String {
            return nome!!
        }
    }


    @Entity(tableName = "borrow")
    data class Borrow(
        @ColumnInfo(name="borrowDate")
        var borrowDate: String?,
        @ColumnInfo(name="returnDate")
        var returnDate: String?,
        @ForeignKey(entity = Item::class, parentColumns = arrayOf("idItem"), childColumns = arrayOf("idItem"), onDelete = ForeignKey.RESTRICT)
        @ColumnInfo(name="idItem")
        var idItem: Int?,
        @ForeignKey(entity = Person::class, parentColumns = arrayOf("idItem"), childColumns = arrayOf("idItem"), onDelete = ForeignKey.RESTRICT)
        @ColumnInfo(name="idPerson")
        var idPerson: Int?
    ){
        @NonNull
        @ColumnInfo(name="id")
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0

        override fun toString(): String {
            return borrowDate!!
        }
    }

    @Entity(tableName = "person")
    data class Person(
        @ColumnInfo(name="name")
        var name: String?,
        @ColumnInfo(name="phone")
        var phone: String?,
        @ColumnInfo(name="cep")
        var cep: String?,
        @ColumnInfo(name="address")
        var address: String?
    ){
        @NonNull
        @ColumnInfo(name="idPerson")
        @PrimaryKey(autoGenerate = true)
        var idPerson: Int = 0

        override fun toString(): String {
            return name!!
        }
    }

}