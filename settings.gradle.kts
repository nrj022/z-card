rootProject.name = "Z Card"
include(":app")
include(":core:analytics")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:network")
include(":feature")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            content { includeGroup("com.github.nrj022") }
        }
        maven { url = uri("unity_repo") }
    }
}
