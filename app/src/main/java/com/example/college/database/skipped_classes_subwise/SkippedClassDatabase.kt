package com.example.college.database.skipped_classes_subwise

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SkippedClassModel::class], version = 1, exportSchema = false)
abstract class SkippedClassDatabase : RoomDatabase() {

    abstract fun skippedClassesDao(): SkippedClassesDao

    companion object {
        @Volatile
        private var INSTANCE: SkippedClassDatabase? = null

        fun getDatabase(context: Context): SkippedClassDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkippedClassDatabase::class.java,
                    "subject_data"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}