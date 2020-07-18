package com.benmohammad.coroutinesflow.data.remote

import com.squareup.moshi.Json

data class UserBody(
    @Json(name = "email")
    val email: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastNAme: String,
    @Json(name = "avatar")
    val avatar: String
)