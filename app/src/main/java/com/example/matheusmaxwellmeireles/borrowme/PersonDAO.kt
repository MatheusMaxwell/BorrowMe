package com.example.matheusmaxwellmeireles.borrowme

import android.app.Person
import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface PersonDAO {

    @Query("SELECT * FROM person")
    fun getAllPerson(): List<Entities.Person>

    @Query(value = "SELECT * FROM person WHERE idPerson=:personId")
    fun getPersonById(personId: Int): Flowable<Entities.Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(vararg users: Entities.Person)

    @Update
    fun updatePerson(user: Entities.Person)

    @Delete
    fun deletePerson(user: Entities.Person)

}