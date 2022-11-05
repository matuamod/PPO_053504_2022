package com.timer.lab2_timer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "items")
data class Item (
    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "color")
    var color: String,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
