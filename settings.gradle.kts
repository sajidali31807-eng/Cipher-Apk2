pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
        flatDir {
            dirs("app/libs", "libs")
        }
    }
}

rootProject.name = "Cipher"
include(":app")
