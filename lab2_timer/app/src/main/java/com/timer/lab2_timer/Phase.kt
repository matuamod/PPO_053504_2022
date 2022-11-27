package com.timer.lab2_timer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "phases",
    foreignKeys = [
        ForeignKey(entity = Item::class,
                    parentColumns = ["id"],
                    childColumns = ["timer_id"],
                    onDelete = CASCADE)
    ])
data class Phase (
    @ColumnInfo(name = "timer_id")
    var timer_id: Int? = null,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "duration")
    var duration: Int,

    @ColumnInfo(name = "rest")
    var rest: Int,

    @ColumnInfo(name = "attempt_count")
    var attempt_count: Int,

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}