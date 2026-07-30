package com.tapasco.characters.core.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CoroutineResultTest {

    @Test
    fun exception_isReturnedAsFailure() = runTest {
        val expectedException = IllegalStateException("Network error")

        val result = runSuspendCatching<Int> {
            throw expectedException
        }

        assertSame(expectedException, result.exceptionOrNull())
    }

    @Test
    fun cancellationException_isRethrown() = runTest {
        val expectedException = CancellationException("Cancelled")

        try {
            runSuspendCatching<Int> {
                throw expectedException
            }
            fail("CancellationException should be rethrown")
        } catch (actualException: CancellationException) {
            assertSame(expectedException, actualException)
        }
    }
}
