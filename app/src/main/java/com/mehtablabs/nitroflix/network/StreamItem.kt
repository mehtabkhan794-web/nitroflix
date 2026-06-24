package com.mehtablabs.nitroflix.network

import com.google.gson.annotations.SerializedName

data class StreamItem(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("thumbnail") val posterUrl: String,
    @SerializedName("url") val streamUrl: String
)