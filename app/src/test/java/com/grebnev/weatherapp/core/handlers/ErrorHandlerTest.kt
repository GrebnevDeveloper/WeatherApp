package com.grebnev.weatherapp.core.handlers

import com.grebnev.weatherapp.core.wrappers.ErrorType
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.sql.SQLException

class ErrorHandlerTest {
    @Test
    fun `getErrorTypeByError should return NETWORK_ERROR for IOException`() {
        val mockIOException: Throwable = mockk<IOException>()

        val result = ErrorHandler.getErrorTypeByError(mockIOException)

        Assert.assertEquals(ErrorType.NETWORK_ERROR, result)
    }

    @Test
    fun `getErrorTypeByError should return DATABASE_ERROR for SQLException`() {
        val mockSQLException: Throwable = mockk<SQLException>()

        val result = ErrorHandler.getErrorTypeByError(mockSQLException)

        Assert.assertEquals(ErrorType.DATABASE_ERROR, result)
    }

    @Test
    fun `getErrorTypeByError should return UNKNOWN_ERROR for other exceptions`() {
        val mockThrowable: Throwable = mockk()

        val result = ErrorHandler.getErrorTypeByError(mockThrowable)

        Assert.assertEquals(ErrorType.UNKNOWN_ERROR, result)
    }
}