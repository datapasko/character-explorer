package com.tapasco.characters.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationReferenceDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
)
