apply(from = "../dsl/android-library.gradle")
apply(from = "../dsl/ktlint.gradle")
apply(from = "../dsl/bintray.gradle")


dependencies {
    /**
     * Kotlin support
     */
    "implementation"("com.eaglesakura.armyknife.armyknife-runtime:armyknife-runtime:1.3.7")
    "implementation"("com.eaglesakura.armyknife.armyknife-jetpack:armyknife-jetpack:1.4.9")
    "implementation"("com.eaglesakura.armyknife.armyknife-android-bundle:armyknife-android-bundle:1.3.0")

    "compileOnly"("androidx.annotation:annotation:1.1.0")
    "compileOnly"("androidx.core:core:1.2.0")
    "compileOnly"("androidx.core:core-ktx:1.2.0")
    "compileOnly"("androidx.appcompat:appcompat:1.1.0")
    "compileOnly"("androidx.appcompat:appcompat-resources:1.1.0")
    "compileOnly"("androidx.lifecycle:lifecycle-extensions:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-runtime:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-reactivestreams:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.2.0")
    "compileOnly"("androidx.lifecycle:lifecycle-service:2.2.0")
}