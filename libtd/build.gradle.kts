plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
}

group = "io.github.tdlibx"
version = "1.8.56-RC10"

kotlin {

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm() // For Desktop (JVM)

    macosX64()
    macosArm64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

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
            val libDir =
                when {
                    target.name.contains("Simulator") || target.name.endsWith("X64") && target.name.startsWith("ios") -> "ios-simulator"
                    target.name.startsWith("ios") -> "ios"
                    else -> "macos"
                }
            linkerOpts("-L${project.file("native_libs/$libDir").absolutePath}", "-ltdjson")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                // Thin core has no mandatory dependencies
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.annotation:annotation:1.9.1")
            }
        }

        // Native targets (iOS, macOS) depend on the published xcframework
        // containing prebuilt libtdjson.dylib. Consumers using the
        // io.github.tdlibx.tdlib-xcframework Gradle plugin will have the
        // binary automatically linked; for local development the
        // libtdjson dylibs at native_libs/{ios,ios-simulator,macos}/
        // must be present (see AGENTS.md).
        //
        // We declare the dep on each native target's intermediate source set
        // to ensure it appears in the klib metadata for iOS/macOS publications
        // but NOT in the Android or JVM POMs (where the xcframework zip is
        // useless and would just add download weight).
        val iosArm64Main by getting {
            dependencies { implementation("io.github.tdlibx:td-libtdjson:1.8.56-RC10") }
        }
        val iosSimulatorArm64Main by getting {
            dependencies { implementation("io.github.tdlibx:td-libtdjson:1.8.56-RC10") }
        }
        val iosX64Main by getting {
            dependencies { implementation("io.github.tdlibx:td-libtdjson:1.8.56-RC10") }
        }
        val macosArm64Main by getting {
            dependencies { implementation("io.github.tdlibx:td-libtdjson:1.8.56-RC10") }
        }
        val macosX64Main by getting {
            dependencies { implementation("io.github.tdlibx:td-libtdjson:1.8.56-RC10") }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
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
    // Include native TDLib JSON libraries
    sourceSets["main"].jniLibs.srcDir("src/androidMain/jniLibs")

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

mavenPublishing {
    coordinates("io.github.tdlibx", "td", "1.8.56-RC10")

    pom {
        name.set("td")
        description.set("Telegram TDLib for Kotlin Multiplatform")
        inceptionYear.set("2026")
        url.set("https://github.com/tdlibx/td")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("tdlibx")
                name.set("tdlibx Contributors")
            }
        }
        scm {
            url.set("https://github.com/tdlibx/td")
            connection.set("scm:git:git://github.com/tdlibx/td.git")
            developerConnection.set("scm:git:ssh://git@github.com/tdlibx/td.git")
        }
    }

    // Configure targeting the modern Sonatype Central Portal
    publishToMavenCentral()

    // Sign all generated multiplatform target publications
    signAllPublications()
}
