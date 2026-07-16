# Telegram Database library for Kotlin Multiplatform

TDLib can be build following [instruction](https://github.com/tdlib/td/tree/master/example/android).

## How to
To get the library into your build:

### Step 1. Add Maven Central repository to your build file

```Gradle
allprojects {
  repositories {
    ...
    mavenCentral()
  }
}
```

### Step 2. Add the dependency

```Gradle
dependencies {
  implementation "io.github.tdlibx:td:1.8.56-RC5"
}
```
