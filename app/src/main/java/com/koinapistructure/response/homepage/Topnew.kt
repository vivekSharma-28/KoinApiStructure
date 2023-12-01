package com.example.theweekin.modelClass.homepage

data class Topnew(
    val ad_desc: String,
    val cats: List<Cat>,
    val comments: Int,
    val date: String,
    val has_image: Boolean,
    val image: String,
    val post_id: Int,
    val read_more: String,
    val title: String
)