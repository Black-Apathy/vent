pluginManagement {
    repositories {
        google() // Google's Maven Repository
        mavenCentral() // Maven Central for Java/Kotlin dependencies
        gradlePluginPortal() // Gradle Plugin Portal
    }
    // Cache plugin dependencies locally
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral {
            content {
                includeGroupByRegex("org\\.jetbrains.*")
                includeGroupByRegex("com\\.github.*")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Vent"
include(":app")