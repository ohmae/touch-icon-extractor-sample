import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gradleVersions)
    alias(libs.plugins.dependencyGuard)

    // for release
}

val applicationName = "WebClip"
val versionMajor = 0
val versionMinor = 5
val versionPatch = 1

android {
    compileSdk = 36

    namespace = "net.mm2d.webclip"
    defaultConfig {
        applicationId = "net.mm2d.webclip"
        minSdk = 21
        targetSdk = 36
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "$versionMajor.$versionMinor.$versionPatch"
        base.archivesName.set("$applicationName-$versionName")
        vectorDrawables.useSupportLibrary = true
    }
    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName =
                    "$applicationName-$versionName.apk"
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    buildFeatures {
        buildConfig = true
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
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(libs.touchicon)
    implementation(libs.touchiconOkhttp)

    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxActivity)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxLifecycleViewModel)
    implementation(libs.androidxPreference)
    implementation(libs.androidxDatastorePreferences)
    implementation(libs.androidxWebkit)
    implementation(libs.material)
    implementation(libs.hiltAndroid)
    ksp(libs.hiltAndroidCompiler)

    implementation(libs.okhttp)
    implementation(libs.coil)
    implementation(libs.coilNetworkOkHttp)

    debugImplementation(libs.leakcanary)
    debugImplementation(libs.bundles.flipper)
    debugImplementation(libs.okhttpLoggingInterceptor)

    // for release
}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}

fun isStable(
    version: String,
): Boolean {
    val hasStableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf { !isStable(candidate.version) && isStable(currentVersion) }
}
