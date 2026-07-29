package com.tapasco.characters.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.domain.model.Character
import retrofit2.HttpException
import java.io.IOException

private const val FIRST_PAGE = 1

class CharactersPagingSource(
    private val api: RickAndMortyApi,
    private val name: String?,
    private val status: String?,
) : PagingSource<Int, Character>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: FIRST_PAGE

        return try {
            val response = api.getCharacters(
                page = page,
                name = name,
                status = status,
            )

            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (response.info.next == null) null else page + 1,
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? = state.anchorPosition?.let { anchorPosition ->
        val anchorPage = state.closestPageToPosition(anchorPosition)
        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}
