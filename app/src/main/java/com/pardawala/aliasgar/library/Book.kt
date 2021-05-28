package com.pardawala.aliasgar.library

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Book")
@Parcelize
data class Book(
    val title: String,
    val image: String,
    val author: String,
    val rating: Float,
    val descripton: String,
    val buy_link: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
