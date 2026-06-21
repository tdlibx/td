plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    kotlin("plugin.serialization")
}

group = "com.github.tdlibx"
version = "1.8.6"

kotlin {

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    macosX64()
    macosArm64()
    
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":td-kmp-core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
                implementation(libs.kotlinx.serialization.json)
            }
        }
        
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
            }
        }
        
        androidMain {
            dependencies {
                api("androidx.annotation:annotation:1.9.1")
            }
        }

        // Instrumented integration tests (run on device/emulator)
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.espresso.core)
            }
        }
    }
}

android {
    // Include native TDLib JSON libraries from the td-kmp-core module
    sourceSets["main"].jniLibs.srcDir("../td-kmp-core/src/androidMain/jniLibs")

    compileSdk = 36
    namespace = "org.drinkless.tdlib"

    
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
        getByName("androidTest") {
            java.srcDirs("src/androidInstrumentedTest/kotlin")
            manifest.srcFile("src/androidInstrumentedTest/AndroidManifest.xml")
        }
    }

    ndkVersion = "30.0.14904198"

    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
                abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            // Allow instrumented tests to run on API 21+
            // (test dependencies like androidx.test.ext:junit require minSdk 21)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    lint {
        disable.add("InvalidPackage")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            artifactId = "td" + (if (name == "kotlinMultiplatform") "" else "-$name")
        }
    }
}
