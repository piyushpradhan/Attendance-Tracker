package com.example.college.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName="subject_data")
data class SkippedClassModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val subjectName: String,
    val classesSkipped: Int
) : Parcelable