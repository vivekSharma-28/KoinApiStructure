package com.koinapistructure.response.newsfeed

data class NewsListing(
    val news: List<New>,
    val text: String
)