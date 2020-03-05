apply(from = "../dsl/android-library.gradle")
apply(from = "../dsl/ktlint.gradle")
apply(from = "../dsl/bintray.gradle")


dependencies {
    /**
     * Kotlin support
     */
    "api"("com.eaglesakura.armyknife.armyknife-runtime:armyknife-runtime:1.3.6")
    "api"("com.eaglesakura.armyknife.armyknife-jetpack:armyknife-jetpack:1.4.9")

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