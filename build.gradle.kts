import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
}

group = "ru.newmcpe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

allprojects {
    tasks {
        val kotlinOptions: org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.() -> Unit = {
            apiVersion = "1.7"
            languageVersion = "1.7"
            jvmTarget = "17"
            freeCompilerArgs = listOf(
                "-Xjvm-default=all",
                "-Xopt-in=kotlin.ExperimentalStdlibApi"
            )
        }
        compileKotlin {
            kotlinOptions(kotlinOptions)
        }
        compileTestKotlin {
            kotlinOptions(kotlinOptions)
        }
    }
}