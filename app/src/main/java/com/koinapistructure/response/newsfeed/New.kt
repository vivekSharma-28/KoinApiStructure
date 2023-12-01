package com.koinapistructure.response.newsfeed

data class New(
    val ad_desc: String,
    val cats: List<Cat>,
    val comments: Int,
    val date: String,
    val has_image: Boolean,
    val image: String,
    val pdf_link: String,
    val post_id: Int,
    val read_more: String,
    val share_link: String,
    val title: String
)