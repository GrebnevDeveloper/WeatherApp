package com.grebnev.weatherapp.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.grebnev.weatherapp.R
import com.grebnev.weatherapp.domain.entity.Forecast
import com.grebnev.weatherapp.domain.entity.Weather
import com.grebnev.weatherapp.presentation.base.SnackbarState
import com.grebnev.weatherapp.presentation.base.actionSnackbar
import com.grebnev.weatherapp.presentation.extensions.formattedFullDate
import com.grebnev.weatherapp.presentation.extensions.formattedShortDayOfWeek
import com.grebnev.weatherapp.presentation.extensions.toTempCString
import com.grebnev.weatherapp.presentation.ui.theme.CardGradients

@Composable
fun DetailsContent(component: DetailsComponent) {
    val state by component.model.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarState = remember { mutableStateOf(SnackbarState.HIDDEN) }

    LaunchedEffect(state.forecastState) {
        snackbarState.value =
            when (state.forecastState) {
                is DetailsStore.State.ForecastState.LoadedFromCache -> SnackbarState.SHOW_CACHE
                DetailsStore.State.ForecastState.Error -> SnackbarState.SHOW_ERROR
                else -> SnackbarState.HIDDEN
            }
    }

    LaunchedEffect(snackbarState.value) {
        when (snackbarState.value) {
            SnackbarState.SHOW_CACHE -> {
                val timeLastUpdate =
                    (state.forecastState as DetailsStore.State.ForecastState.LoadedFromCache).timeLastUpdate
                actionSnackbar(
                    snackbarHostState = snackbarHostState,
                    context = context,
                    message = "${context.getString(R.string.data_from_memory)} $timeLastUpdate",
                    action = component::onRetryLoadForecastClick,
                )
            }
            SnackbarState.SHOW_ERROR ->
                actionSnackbar(
                    snackbarHostState = snackbarHostState,
                    context = context,
                    message = context.getString(R.string.error_load_data),
                    action = component::onRetryLoadForecastClick,
                )
            SnackbarState.HIDDEN -> snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        modifier =
            Modifier
                .fillMaxSize()
                .background(CardGradients.gradients[(0..4).random()].primaryGradient),
        topBar = {
            TopBar(
                cityName = state.city.name,
                isFavouriteCity = state.isFavourite,
                onBackClick = {
                    component.onBackClick()
                },
                onChangedFavouriteStatusClick = {
                    component.onFavouriteStatusClick()
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val forecastState = state.forecastState) {
                DetailsStore.State.ForecastState.Error -> {
                    Error()
                }

                DetailsStore.State.ForecastState.Initial -> {
                    Initial()
                }

                is DetailsStore.State.ForecastState.Loaded -> {
                    Loaded(forecastState.forecast)
                }

                DetailsStore.State.ForecastState.Loading -> {
                    Loading()
                }

                is DetailsStore.State.ForecastState.LoadedFromCache -> {
                    Loaded(forecastState.forecast)
                }
            }
        }
    }
}

@Composable
private fun Error() {
    Box(modifier = Modifier.fillMaxSize()) {
        Icon(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(70.dp),
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
        )
    }
}

@Composable
private fun Initial() {
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.background,
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Loaded(forecast: Forecast) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = forecast.currentWeather.conditionText,
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = forecast.currentWeather.tempC.toTempCString(),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 70.sp),
            )
            GlideImage(
                modifier = Modifier.size(70.dp),
                model = forecast.currentWeather.conditionUrl,
                contentDescription = null,
            )
        }
        Text(
            text = forecast.currentWeather.date.formattedFullDate(),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        AnimatedUpcomingWeather(forecast.upcoming)
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun AnimatedUpcomingWeather(upcoming: List<Weather>) {
    val state =
        remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
        }

    AnimatedVisibility(
        visibleState = state,
        enter =
            fadeIn(animationSpec = tween(500)) +
                slideIn(
                    animationSpec = tween(500),
                    initialOffset = { IntOffset(x = 0, y = it.height) },
                ),
    ) {
        UpcomingWeather(upcoming)
    }
}

@Composable
private fun UpcomingWeather(upcoming: List<Weather>) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.text_upcoming),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                upcoming.forEach {
                    SmallWeatherCard(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RowScope.SmallWeatherCard(weather: Weather) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        modifier =
            Modifier
                .height(128.dp)
                .weight(1f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = weather.tempC.toTempCString())
            GlideImage(
                modifier = Modifier.size(48.dp),
                model = weather.conditionUrl,
                contentDescription = null,
            )
            Text(text = weather.date.formattedShortDayOfWeek())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    cityName: String,
    isFavouriteCity: Boolean,
    onBackClick: () -> Unit,
    onChangedFavouriteStatusClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors =
            TopAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
        title = { Text(text = cityName) },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = { onChangedFavouriteStatusClick() }) {
                val icon =
                    if (isFavouriteCity) {
                        Icons.Default.Star
                    } else {
                        Icons.Default.StarBorder
                    }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        },
    )
}