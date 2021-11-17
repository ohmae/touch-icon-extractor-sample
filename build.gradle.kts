buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(kotlin("gradle-plugin", version = "1.6.0"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.39.0")
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
