package com.grebnev.weatherapp.presentation.base

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.grebnev.weatherapp.R

suspend fun actionSnackbar(
    snackbarHostState: SnackbarHostState,
    context: Context,
    message: String,
    action: () -> Unit,
) {
    val snackbarResult =
        snackbarHostState.showSnackbar(
            message = message,
            actionLabel = context.getString(R.string.retry),
            duration = SnackbarDuration.Indefinite,
        )

    if (snackbarResult == SnackbarResult.ActionPerformed) {
        action()
    }
}

enum class SnackbarState {
    HIDDEN,
    SHOW_CACHE,
    SHOW_ERROR,
}