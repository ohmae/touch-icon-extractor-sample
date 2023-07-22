import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.github.ben-manes.versions")

    // for release
}

val applicationName = "WebClip"
val versionMajor = 0
val versionMinor = 4
val versionPatch = 4

android {
    compileSdk = 33

    namespace = "net.mm2d.webclip"
    defaultConfig {
        applicationId = "net.mm2d.webclip"
        minSdk = 21
        targetSdk = 33
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
        base.archivesName.set("${applicationName}-${versionName}")
        vectorDrawables.useSupportLibrary = true
    }
    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName =
                    "${applicationName}-${versionName}.apk"
            }
        }
    }
    buildTypes {
        debug {
            isDebuggable = true
            enableAndroidTestCoverage = true
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlin {
        jvmToolchain(11)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    lint {
        abortOnError = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation("net.mm2d.touchicon:touchicon:0.9.6")
    implementation("net.mm2d.touchicon:touchicon-http-okhttp:0.9.6")

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.webkit:webkit:1.7.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-android-compiler:2.47")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.coil-kt:coil:2.4.0")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
    debugImplementation("com.facebook.flipper:flipper:0.201.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.5")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.201.0")
    debugImplementation("com.facebook.flipper:flipper-leakcanary2-plugin:0.201.0")

    // for release
}

fun isStable(version: String): Boolean {
    val hasStableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf { !isStable(candidate.version) }
}
