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
val versionMinor = 3
val versionPatch = 1

android {
    compileSdk = 31

    namespace = "net.mm2d.webclip"
    defaultConfig {
        applicationId = "net.mm2d.webclip"
        minSdk = 21
        targetSdk = 31
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
            isTestCoverageEnabled = true
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
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("net.mm2d.touchicon:touchicon:0.9.3")
    implementation("net.mm2d.touchicon:touchicon-http-okhttp:0.9.3")

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.webkit:webkit:1.4.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.google.dagger:hilt-android:2.42")
    kapt("com.google.dagger:hilt-android-compiler:2.42")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
    debugImplementation("com.facebook.flipper:flipper:0.148.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.3")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.148.0")
    debugImplementation("com.facebook.flipper:flipper-leakcanary2-plugin:0.148.0")

    // for release
}

fun isStable(version: String): Boolean {
    val hasStableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf { !isStable(candidate.version) }
}
