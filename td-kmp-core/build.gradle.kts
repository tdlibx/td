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
        commonMain
        androidMain
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.getByName("main") {
            val tdjson by cinterops.creating {
                definitionFile.set(project.file("src/nativeInterop/cinterop/tdjson.def"))
                includeDirs {
                    allHeaders(project.file("native_libs/include"))
                }
            }
        }
        
        binaries.all {
            val libDir = when {
                target.name.contains("Simulator") -> "ios-simulator"
                target.name.startsWith("ios") -> "ios"
                else -> "macos"
            }
            linkerOpts("-L${project.file("native_libs/$libDir").absolutePath}", "-ltdjson")
        }
    }
}

android {
    compileSdk = 36
    namespace = "org.drinkless.tdlib.core"
    defaultConfig {
        minSdk = 10
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/androidMain/jniLibs")
        }
    }
}
