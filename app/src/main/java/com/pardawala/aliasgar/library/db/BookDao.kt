package com.pardawala.aliasgar.library.db

import androidx.room.*
import com.pardawala.aliasgar.library.Book

@Dao
interface BookDao {

    @Query("SELECT * FROM Book")
    suspend fun getBooks() : List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)
}