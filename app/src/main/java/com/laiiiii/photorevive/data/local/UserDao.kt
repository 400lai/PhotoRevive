package com.laiiiii.photorevive.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE account = :account LIMIT 1")
    suspend fun getUserByAccount(account: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserByIdFlow(userId: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserById(userId: Long): User?
}