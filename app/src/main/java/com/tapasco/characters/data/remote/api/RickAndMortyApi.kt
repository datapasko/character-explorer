package com.tapasco.characters.data.remote.api

import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
    ): CharactersResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") characterId: Int,
    ): CharacterDto
}
