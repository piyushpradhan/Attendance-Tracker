package com.example.college.database.skipped_classes_subwise

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.college.models.SkippedClassModel

@Dao
abstract interface SkippedClassesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubject(skippedClassModel: SkippedClassModel)

    @Query("SELECT * FROM subject_data")
    fun readAllData() : LiveData<List<SkippedClassModel>>

    @Delete
    suspend fun deleteSubject(skippedClassModel: SkippedClassModel)

    @Update
    suspend fun updateData(skippedClassModel: SkippedClassModel)

}