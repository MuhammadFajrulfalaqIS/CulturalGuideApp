package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String, // UID dari Firebase
    val name: String,
    val email: String,
    val profileImagePath: String? = null // Path ke file lokal
)