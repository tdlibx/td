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
        commonMain {
            dependencies {
                implementation(project(":td-kmp-core"))
            }
        }
        
        androidMain {
            dependencies {
                api("androidx.annotation:annotation:1.9.1")
            }
        }
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
