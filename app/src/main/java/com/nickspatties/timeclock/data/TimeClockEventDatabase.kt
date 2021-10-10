package com.nickspatties.timeclock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeClockEvent::class], version = 1, exportSchema = false)
public abstract class TimeClockEventDatabase : RoomDatabase() {
    abstract val timeClockEventDao: TimeClockEventDao

    companion object {

        // this data will never be cached and will always be obtained from main memory
        // this means that the db will be the same for all threads
        @Volatile
        private var INSTANCE: TimeClockEventDatabase? = null

        fun getInstance(context: Context): TimeClockEventDatabase {
            // only one thread can execute this block at a time
            // this avoids two threads creating a db at the same time, leading to two dbs
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TimeClockEventDatabase::class.java,
                        "sleep_history_database"
                    )
                        // destroy and rebuild db when changing versions
                        // normally you'd provide a migration strategy when changing versions via a migration object
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
