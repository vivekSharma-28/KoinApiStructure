package com.koinapistructure.response.newsfeed

data class newsfeed(
    val message: String,
    val news_listing: NewsListing,
    val pagination: Pagination,
    val status: Boolean
)