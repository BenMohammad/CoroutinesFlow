package com.benmohammad.coroutinesflow.domain.entity

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatar: String
)