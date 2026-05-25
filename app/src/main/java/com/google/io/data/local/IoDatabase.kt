package com.google.io.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.io.data.model.Session

/**
 * This is the big boss: The Room Database.
 * It's the actual file on the phone that stores our data.
 */
@Database(entities = [Session::class], version = 1, exportSchema = false)
abstract class IoDatabase : RoomDatabase() {
    
    // We connect our instructions (DAO) to the database here
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: IoDatabase? = null

        /**
         * This helper function makes sure we only have ONE database open at a time.
         * Having two would be like trying to write in the same notebook with two pens at once!
         */
        fun getDatabase(context: Context): IoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IoDatabase::class.java,
                    "io_database" // The name of our database file
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
