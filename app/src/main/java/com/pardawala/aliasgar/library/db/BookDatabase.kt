package com.pardawala.aliasgar.library.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pardawala.aliasgar.library.Book
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [Book::class],
    version = 1)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object{
        @Volatile
        private var instance: BookDatabase? = null
        private val LOCK = Any()

        @InternalCoroutinesApi
        fun getInstance(context: Context) : BookDatabase {
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book.db"
                ).build() .also {
                    instance = it
                }
            }
        }

//        @InternalCoroutinesApi
//        operator fun invoke(context: Context) = instance ?: synchronized(
//            LOCK
//        ) {
//            instance ?: createDatabase(context).also { instance = it }
//        }
//
//        private fun createDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                BookDatabase::class.java,
//                "book_db.db"
//            ).build()
    }
}