plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "com.github.tdlibx"
version = "1.8.6"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    
    macosX64()
    macosArm64()
    
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":td-kmp-core"))
            }
        }
        
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.annotation:annotation:1.9.1")
            }
        }
        
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
    namespace = "org.drinkless.tdlib"
    
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
            jniLibs.srcDir("src/androidMain/jniLibs")
            jni.srcDirs() // disable automatic ndk-build call
        }
    }

    defaultConfig {
        minSdk = 10
        // targetSdk is not strictly required for libraries
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

// Ensure the artifactId remains 'td' for the primary publication if needed.
// Note: KMP publishing handles multiple targets; this primarily affects the metadata/android publication.
publishing {
    publications {
        withType<MavenPublication> {
            artifactId = "td" + (if (name == "kotlinMultiplatform") "" else "-$name")
        }
    }
}
