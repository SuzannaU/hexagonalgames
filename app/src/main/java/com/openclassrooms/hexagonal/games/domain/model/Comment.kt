package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

data class Comment(
    val author: User? = null,
    val content: String = "",
    val dateCreated: Long = System.currentTimeMillis()
) : Serializable
