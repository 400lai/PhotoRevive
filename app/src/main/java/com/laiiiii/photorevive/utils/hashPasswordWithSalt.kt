package com.laiiiii.photorevive.utils

import java.security.MessageDigest

fun hashPasswordWithSalt(password: String, salt: String): String {
    val input = "$password$salt"
    return MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray())
        .fold("", { str, it -> str + "%02x".format(it) })
}