package com.pkt.majika.utils.retrofit

import retrofit2.Response
import retrofit2.http.GET

interface EndpointMenu {
    @GET("/v1/menu")
    suspend fun getMenu() : Response<DatasMenu>
}