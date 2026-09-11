pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HotelExplorer"

include(":app")

include(":core:common")
include(":core:network")
include(":core:database")
include(":core:domain")
include(":core:designsystem")

include(":data")
