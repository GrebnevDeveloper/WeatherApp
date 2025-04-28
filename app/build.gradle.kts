import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.grebnev.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grebnev.weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val key =
            property("apikey")?.toString() ?: error(
                "You need to add the api key to the file gradle.properties",
            )
        buildConfigField("String", "WEATHER_API_KEY", "\"$key\"")
    }

    applicationVariants.all {
        outputs.all {
            val versionName = versionName
            val versionCode = versionCode
            val buildType = buildType.name
            val date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())

            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "WeatherApp_${buildType}_${versionName}_${versionCode}_$date.apk"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.bundles.mvikotlin)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.ktor)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.room.core)
    implementation(libs.room.coroutines)
    ksp(libs.room.compiler)

    implementation(libs.work.runtime)

    implementation(libs.glide)
    implementation(libs.icons)

    implementation(libs.timber.log)

    implementation(libs.androidx.splash)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.test)
    testImplementation(libs.robolectric.test)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine.test)
    testImplementation(libs.work.test)
    testImplementation(libs.logger.test)
    testImplementation(libs.ktor.test)
    testImplementation(libs.hilt.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}