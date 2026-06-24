package com.mehtablabs.nitroflix.network

import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getCustomStreams(@Url url: String): List<StreamItem>
}