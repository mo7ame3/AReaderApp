package com.example.areader.model.apiModel

data class Book(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)