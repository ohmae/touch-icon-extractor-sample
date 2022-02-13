buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.42.0")
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
