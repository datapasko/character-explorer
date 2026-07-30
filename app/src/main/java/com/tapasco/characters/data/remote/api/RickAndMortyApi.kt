package com.tapasco.characters.data.remote.api

import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import com.tapasco.characters.data.remote.dto.EpisodeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("status") status: String?,
    ): CharactersResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") characterId: Int,
    ): CharacterDto

    @GET("episode/{id}")
    suspend fun getEpisode(
        @Path("id") episodeId: Int,
    ): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodes(
        @Path("ids") episodeIds: String,
    ): List<EpisodeDto>
}
