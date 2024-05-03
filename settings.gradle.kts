pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven{
            url=uri("https://sdk.uxcam.com/android/")
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven{
            url=uri("https://sdk.uxcam.com/android/")
        }
    }
}

rootProject.name = "KoinApiStructure"
include(":app", ":ucrop",":bannerview", ":indicator")