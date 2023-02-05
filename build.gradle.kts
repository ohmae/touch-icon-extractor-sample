plugins {
    id("com.android.application") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("dagger.hilt.android.plugin") version "2.44.2" apply false
    id("com.github.ben-manes.versions") version "0.45.0" apply false

    // for release
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
