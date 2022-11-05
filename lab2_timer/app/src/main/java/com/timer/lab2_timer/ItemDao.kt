package com.timer.lab2_timer

import androidx.room.*
import androidx.room.Dao

@Dao
interface ItemDao {

    // Suspend fun will not stopped the Coroutine
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAllItems()
}