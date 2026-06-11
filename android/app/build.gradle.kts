import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

android {
    namespace = "com.minerva.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.minerva.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-alpha"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MINERVA_BASE_URL",
            "\"${localProps["MINERVA_BASE_URL"] ?: "http://10.0.2.2:8000/"}\"")
        buildConfigField("String", "SUPABASE_URL",
            "\"${localProps["SUPABASE_URL"] ?: "https://placeholder.supabase.co"}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY",
            "\"${localProps["SUPABASE_ANON_KEY"] ?: ""}\"")
    }

    signingConfigs {
        create("release") {
            // Credenciales de firma fuera del control de versiones:
            // local.properties (local) o variables de entorno (CI).
            storeFile = rootProject.file(
                localProps.getProperty("MINERVA_KEYSTORE_FILE")
                    ?: System.getenv("MINERVA_KEYSTORE_FILE") ?: "minerva.jks")
            storePassword = localProps.getProperty("MINERVA_KEYSTORE_PASSWORD")
                ?: System.getenv("MINERVA_KEYSTORE_PASSWORD")
            keyAlias = localProps.getProperty("MINERVA_KEY_ALIAS")
                ?: System.getenv("MINERVA_KEY_ALIAS") ?: "minerva"
            keyPassword = localProps.getProperty("MINERVA_KEY_PASSWORD")
                ?: System.getenv("MINERVA_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
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
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.activity.compose)
   implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.lottie.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
