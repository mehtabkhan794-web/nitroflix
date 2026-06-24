package com.mehtablabs.nitroflix.network

import com.google.gson.annotations.SerializedName

data class StreamItem(
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("stream_url") val streamUrl: String
)