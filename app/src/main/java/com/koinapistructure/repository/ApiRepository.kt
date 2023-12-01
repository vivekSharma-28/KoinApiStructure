package com.koinapistructure.repository

import com.koinapistructure.Apirequest.Request
import com.koinapistructure.api.Api
import com.koinapistructure.utils.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApiRepository(private val api:Api) {

    suspend fun getData() = flow {
        emit(DataStatus.loading())
        val result=api.getProduct()
        when(result.code()){
            200->emit(DataStatus.success(result.body()))
            400->emit(DataStatus.error(result.message()))
            500->emit(DataStatus.error(result.message()))
        }
    }
        .catch {
            emit(DataStatus.error(it.message.toString()))
        }
        .flowOn(Dispatchers.IO)

    suspend fun getProduct(request: Request) = flow {
        emit(DataStatus.loading())
        val result=api.newsfeed(request)
        when(result.code()){
            200->emit(DataStatus.success(result.body()))
            400->emit(DataStatus.error(result.message()))
            500->emit(DataStatus.error(result.message()))
        }
    }
        .catch {
            emit(DataStatus.error(it.message.toString()))
        }
        .flowOn(Dispatchers.IO)


}
