package com.example.college.database.skipped_classes_subwise

import androidx.lifecycle.LiveData

class SkippedClassRepository(private val skippedClassesDao: SkippedClassesDao) {

    val readAllData: LiveData<List<SkippedClassModel>> = skippedClassesDao.readAllData()

    suspend fun addSubject(skippedClassModel: SkippedClassModel) {
        skippedClassesDao.addSubject(skippedClassModel)
    }

    suspend fun deleteSubject(skippedClassModel: SkippedClassModel) {
        skippedClassesDao.deleteSubject(skippedClassModel)
    }

    suspend fun updateData(skippedClassModel: SkippedClassModel) {
        skippedClassesDao.updateData(skippedClassModel)
    }
}