package com.matugr.sample.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room data class that holds all the (relevant) information to be persisted locally from a token
 * response.
 */
@Entity
data class TokenInfo(
    val accessToken: String,
    val expirationTime: Long, // Epoch
    val refreshToken: String?,
    @PrimaryKey val id: Int = 1 //force single row
)