plugins {
    application
}

dependencies {
    implementation(project(":mc-proxy-api"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    api("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.0")
}

application {
    mainClass.set("MainKt")
}