package com.laiiiii.photorevive.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.laiiiii.photorevive.utils.hashPasswordWithSalt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "photorevive_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 使用原始 SQL 插入默认用户
                            insertDefaultUser(db, context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertDefaultUser(db: SupportSQLiteDatabase, context: Context) {
            GlobalScope.launch {
                val hashedPassword = hashPasswordWithSalt("123456ByteDance", "random_salt_123")

                db.execSQL(
                    """
                    INSERT INTO users (
                        account, username, password_hash, salt, avatar_url, bio, 
                        follow_count, follower_count, usage_count, is_vip, 
                        created_at, updated_at, status
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        "ByteDance@xingtu",
                        "醒小图",
                        hashedPassword,
                        "random_salt_123",
                        "https://github.com/400lai/PhotoRevive/blob/608b3e4fe145bc75812fd33cc49769479d4dad29/app/src/main/res/drawable/app_logo.png",
                        "拍照出片就用醒图！我是醒小图！",
                        221,
                        48,
                        0,
                        0, // is_vip as INTEGER
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        0
                    )
                )
            }
        }
    }
}
