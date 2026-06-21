plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "com.github.tdlibx"
version = "1.8.6"

kotlin {
    androidTarget()
    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val macosX64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }
        val iosArm64Main by getting { dependsOn(nativeMain) }
        val iosSimulatorArm64Main by getting { dependsOn(nativeMain) }
    }
}

android {
    compileSdk = 36
    namespace = "org.drinkless.tdlib.core"
    defaultConfig {
        minSdk = 10
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/androidMain/jniLibs")
        }
    }
}
