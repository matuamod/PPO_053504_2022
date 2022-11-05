package com.timer.lab2_timer

import androidx.room.*

@Dao
interface PhaseDao {

    // Suspend fun will not stopped the Coroutine
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhase(phase: Phase)

    @Query("SELECT * FROM phases")
    suspend fun getAllPhases(): List<Phase>

    @Update
    suspend fun updatePhase(phase: Phase)

    @Delete
    suspend fun deletePhase(phase: Phase)

    @Query("DELETE FROM phases")
    suspend fun deleteAllPhases()
}