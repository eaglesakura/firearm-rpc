buildscript {
    rootProject.extra["kotlin_version"] = "1.3.61"
    rootProject.extra["kotlin_coroutines_version"] = "1.3.2"
    rootProject.extra["android_studio_version"] = "3.6.1"
    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extra["android_studio_version"]}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlin_version"]}")
        classpath("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17") // kotlin-docs
        classpath("com.github.ben-manes:gradle-versions-plugin:0.21.0") // version checking plugin

        // deploy to bintray
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://dl.bintray.com/eaglesakura/maven/")
    }
    apply(from = rootProject.file("configure.gradle.kts"))
}
