package com.koinapistructure.Apirequest


data class Request(val category_id: Int,
              val news_per_page: Int,
              val page_no: Int)