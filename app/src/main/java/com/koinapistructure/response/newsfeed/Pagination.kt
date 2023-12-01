package com.koinapistructure.response.newsfeed

data class Pagination(
    val current_page: Int,
    val total_pages: Int,
    val total_record: Int
)