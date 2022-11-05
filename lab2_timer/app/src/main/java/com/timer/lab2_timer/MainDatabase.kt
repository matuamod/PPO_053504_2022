package com.timer.lab2_timer

import android.content.Context
import androidx.room.*

@Database (
    entities = [Item::class, Phase::class],
    version = 1
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun getItemDao(): ItemDao
    abstract fun getPhaseDao(): PhaseDao

    companion object {

        @Volatile
        private var INSTANCE: MainDatabase? = null
        // Volatile annotation, which means the results will be visible to other threads

        fun getDatabase(context: Context): MainDatabase {
            // If the INSTANCE is not null, then return it,
            // If it is, then create the database
            if(INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }


        private fun buildDatabase(context: Context): MainDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java,
                "timer.db"
            ).build()
        }

    }
}