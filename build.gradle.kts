plugins {
    id("com.android.application") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("dagger.hilt.android.plugin") version "2.44" apply false
    id("com.github.ben-manes.versions") version "0.42.0" apply false

    // for release
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
