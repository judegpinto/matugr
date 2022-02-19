package com.matugr.sample.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room [Database] intended for token persistence.
 */
@Database(entities = [TokenInfo::class], version = 1)
abstract class AuthDatabase: RoomDatabase() {
    abstract fun authDao(): AuthDao
}