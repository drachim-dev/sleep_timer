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

    androidResources {
        generateLocaleConfig = true
        localeFilters += listOf("en", "de", "es")
    }

    defaultConfig {
        applicationId = "dr.achim.sleep_timer"
        minSdk = 29
        targetSdk = 37
        versionCode = (project.findProperty("versionCode") as? String)?.toIntOrNull() ?: 1
        versionName = project.findProperty("versionName") as? String ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties().apply {
            if (localPropertiesFile.exists()) {
                FileInputStream(localPropertiesFile).use { load(it) }
            }
        }

        fun getProperty(name: String): String {
            return System.getenv(name)
                ?: if (localPropertiesFile.exists()) {
                    localProperties.getProperty(name) ?: error("Property '$name' not found in local.properties")
                } else {
                    error("local.properties file not found")
                }
        }

        val revenueCatKey = getProperty("REVENUECAT_KEY")
        buildConfigField("String", "REVENUECAT_KEY", "\"${revenueCatKey}\"")

        val admobAppId = getProperty("ADMOB_APP_ID")
        val admobInterstitialUnitId = getProperty("ADMOB_INTERSTITIAL_UNIT_ID")

        manifestPlaceholders["ADMOB_APP_ID"] = admobAppId
        buildConfigField("String", "ADMOB_INTERSTITIAL_UNIT_ID", "\"${admobInterstitialUnitId}\"")
    }

    signingConfigs {
        create("release") {
            // CI=true is exported by Codemagic
            if (System.getenv("CI") == "true") {
                storeFile = file(System.getenv("CM_KEYSTORE_PATH"))
                storePassword = System.getenv("CM_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("CM_KEY_ALIAS")
                keyPassword = System.getenv("CM_KEY_PASSWORD")
            } else {
                val keystoreProperties = Properties().apply {
                    val keystoreFile = rootProject.file("keystore.properties")
                    if (keystoreFile.exists()) {
                        load(FileInputStream(keystoreFile))
                    } else {
                        error("keystore.properties file not found")
                    }
                }

                keyAlias = keystoreProperties["keyAlias"] as String?
                keyPassword = keystoreProperties["keyPassword"] as String?
                storeFile = keystoreProperties["storeFile"]?.let { file(it as String) }
                storePassword = keystoreProperties["storePassword"] as String?
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.service)
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
    implementation(libs.play.review)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
