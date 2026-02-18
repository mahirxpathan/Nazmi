package com.mahiri.nazmi.model

data class Poem(
    var id: Long = 0,
    var content: String = "",
    var categoryId: Long = 0,
    var isFavorite: Boolean = false
)