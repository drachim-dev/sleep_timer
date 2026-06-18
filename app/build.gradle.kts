import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.aboutLibraries)
}

aboutLibraries {}

koinCompiler {
    compileSafety = true
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "dr.achim.sleep_timer"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "dr.achim.sleep_timer"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        fun getProperty(name: String, default: String): String {
            return if (System.getenv(name) != null) {
                System.getenv(name)
            } else {
                val localProperties = Properties().apply {
                    val localPropertiesFile = rootProject.file("local.properties")
                    if (localPropertiesFile.exists()) {
                        load(FileInputStream(localPropertiesFile))
                    }
                }
                localProperties.getProperty(name, default)
            }
        }

        val revenueCatKey = getProperty("REVENUECAT_KEY", "")
        buildConfigField("String", "REVENUECAT_KEY", "\"${revenueCatKey}\"")

        val admobAppId = getProperty("ADMOB_APP_ID", "")
        val admobInterstitialUnitId = getProperty("ADMOB_INTERSTITIAL_UNIT_ID", "")

        manifestPlaceholders["ADMOB_APP_ID"] = admobAppId
        buildConfigField("String", "ADMOB_INTERSTITIAL_UNIT_ID", "\"${admobInterstitialUnitId}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.annotations)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.revenuecat.purchases)
    implementation(libs.confettikit)
    implementation(libs.play.services.ads)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
