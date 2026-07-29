package com.tapasco.characters.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("origin")
    val origin: LocationReferenceDto,
    @SerializedName("location")
    val location: LocationReferenceDto,
    @SerializedName("image")
    val image: String,
    @SerializedName("episode")
    val episodes: List<String>,
    @SerializedName("url")
    val url: String,
    @SerializedName("created")
    val created: String,
)
