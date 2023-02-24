package com.pkt.majika.utils.retrofit

import retrofit2.Response
import retrofit2.http.GET

interface EndpointBranch {
    @GET("/v1/branch")
    suspend fun getBranch() : Response<DatasBranch>
}