package com.tapasco.characters.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharactersResponseDto(
    @SerializedName("info")
    val info: PageInfoDto,
    @SerializedName("results")
    val results: List<CharacterDto>,
)
