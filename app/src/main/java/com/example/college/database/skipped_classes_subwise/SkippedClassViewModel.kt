package com.example.college.database.skipped_classes_subwise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.college.models.SkippedClassModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkippedClassViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<SkippedClassModel>>
    val repository: SkippedClassRepository

    init {
        val skippedClassesDao = SkippedClassDatabase.getDatabase(application).skippedClassesDao()
        repository = SkippedClassRepository(skippedClassesDao)
        readAllData = repository.readAllData
    }

    fun addSubject(skippedClassModel: SkippedClassModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSubject(skippedClassModel)
        }
    }

    fun deleteSubject(skippedClassModel: SkippedClassModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubject(skippedClassModel)
        }
    }

    fun updateData(skippedClassModel: SkippedClassModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(skippedClassModel)
        }
    }

}