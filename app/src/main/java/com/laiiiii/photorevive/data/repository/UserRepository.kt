package com.laiiiii.photorevive.data.repository

import com.laiiiii.photorevive.data.local.AppPreferences
import com.laiiiii.photorevive.data.local.User
import com.laiiiii.photorevive.data.local.UserDao
import com.laiiiii.photorevive.utils.hashPasswordWithSalt
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(
    private val userDao: UserDao,
    private val appPreferences: AppPreferences
) {
    suspend fun loginUser(account: String, password: String): Result<Long> {
        val user = userDao.getUserByAccount(account)
        if (user == null) {
            return Result.failure(Exception("账号不存在"))
        }

        val hashedInput = hashPasswordWithSalt(password, user.salt)
        if (hashedInput != user.passwordHash) {
            return Result.failure(Exception("密码错误"))
        }

        if (user.status != 0) {
            return Result.failure(Exception("账号已被冻结"))
        }

        appPreferences.setLoggedIn(user.userId)
        return Result.success(user.userId)
    }

    suspend fun registerUser(account: String, username: String, password: String): Result<Long> {
        // 检查账号是否已存在
        if (userDao.getUserByAccount(account) != null) {
            return Result.failure(Exception("账号已存在"))
        }

        val salt = generateSalt()
        val passwordHash = hashPasswordWithSalt(password, salt)

        val newUser = User(
            account = account,
            username = username.ifEmpty { "用户$account" },
            passwordHash = passwordHash,
            salt = salt
        )

        val userId = userDao.insertUser(newUser)
        return Result.success(userId)
    }

    suspend fun getCurrentUser(): User? {
        val userId = appPreferences.currentUserId.firstOrNull() ?: return null
        return userDao.getUserById(userId)
    }

    private fun generateSalt(): String {
        return java.util.UUID.randomUUID().toString().substring(0, 16)
    }
}