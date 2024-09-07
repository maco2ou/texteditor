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
        jcenter()
        maven { url = uri("https://jitpack.io")}
        maven("https://oss.sonatype.org/content/repositories/snapshots/")

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io")}
        maven("https://oss.sonatype.org/content/repositories/snapshots/")

    }

}

rootProject.name = "Text Editor"

include(":app")
include(":libsettings")
include(":libRunner")
include(":libEditor")
