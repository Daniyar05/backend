import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    alias(libs.plugins.configuration)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildconfig)
}

configuration {
    internal {
        +projects.core.data
        +projects.core.domain
        +projects.core.ktor
        +projects.core.db

        +projects.auth.data

        +libs.bundles.ktor
        +libs.bundles.exposed
        +libs.koin.core
        +libs.koin.ktor
        +libs.h2db
        +libs.serialization
    }
    test {
        +libs.ktor.test
        +libs.kotlin.test
        +libs.kotlin.test.junit
        +projects.core.test
    }
}

buildConfig {
    val properties = Properties().apply {
        load(File(rootDir, "local.properties").bufferedReader())
    }
    buildConfigField("SIGNIN_AUDIENCE", properties.getProperty("SIGNIN_AUDIENCE"))
    buildConfigField("SIGNIN_ISSUER", properties.getProperty("SIGNIN_ISSUER"))
    buildConfigField("SIGNIN_EXPIRATION_TIME", properties.getProperty("SIGNIN_EXPIRATION_TIME").toLong())
    buildConfigField("SIGNIN_SECRET", properties.getProperty("SIGNIN_SECRET"))
}