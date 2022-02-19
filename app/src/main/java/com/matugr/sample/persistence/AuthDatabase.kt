package com.matugr.sample.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TokenInfo::class], version = 1)
abstract class AuthDatabase: RoomDatabase() {
    abstract fun authDao(): AuthDao
}