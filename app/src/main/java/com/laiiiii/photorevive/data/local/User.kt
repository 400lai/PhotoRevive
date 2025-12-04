package com.laiiiii.photorevive.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// User.kt 示例结构
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Long = 0,

    @ColumnInfo(name = "account")
    val account: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "salt")
    val salt: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "follow_count")
    val followCount: Int = 0,

    @ColumnInfo(name = "follower_count")
    val followerCount: Int = 0,

    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,

    @ColumnInfo(name = "is_vip")
    val isVip: Boolean = false,

    @ColumnInfo(name = "vip_expire_time")
    val vipExpireTime: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status")
    val status: Int = 0
)
