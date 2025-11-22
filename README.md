# Telegram Database library for Android

[![](https://jitpack.io/v/tdlibx/td.svg)](https://jitpack.io/#tdlibx/td)

This is a library fetched from the original [TdLib](https://github.com/tdlib/td) for publishing it in Jitpack. 

TDLib can be build following [instruction](https://github.com/tdlib/td/tree/master/example/android).

## How to
To get the library into your build:

### Step 1. Add the JitPack repository to your build file

Add it in your root ``build.gradle`` at the end of repositories:

```Gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

### Step 2. Add the dependency
```Gradle
dependencies {
  implementation 'com.github.tdlibx:td:1.8.56'
}
```
