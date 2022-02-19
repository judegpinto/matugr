package com.matugr.sample.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthDao {
    @Query("SELECT * FROM tokeninfo LIMIT 1")
    fun getTokenInfo(): TokenInfo?

    @Query("DELETE FROM tokeninfo")
    fun deleteTokenInfo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveToken(tokenInfo: TokenInfo)
}