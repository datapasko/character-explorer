package com.tapasco.characters.core.utils

import kotlinx.coroutines.CancellationException

suspend inline fun <T> runSuspendCatching(
    crossinline block: suspend () -> T,
): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Result.failure(exception)
}
