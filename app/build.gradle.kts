plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlin)
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.roomPlugin)
}

android {
    namespace = "ru.application.homemedkit"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.application.homemedkit"
        minSdk = 26
        targetSdk = 34
        versionCode = 24
        versionName = "1.3.4"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
}

dependencies {

    // ==================== Android ====================
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)

    // ==================== BOMs ====================
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.kotlin.bom))

    // ==================== Room ====================
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // ==================== Retrofit ====================
    implementation(libs.retrofit)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    // ==================== Navigation ====================
    ksp(libs.compose.destinations.ksp)
    implementation(libs.compose.destinations)

    // ==================== Scanner ====================
    implementation(libs.code.scanner)

    // ==================== Coil ====================
    implementation(libs.coil.compose)

    // ==================== Settings ====================
    implementation(libs.librarySettingsM3)
}