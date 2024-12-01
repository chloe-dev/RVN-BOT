/////////////
// RVN-BOT //
/////////////

group       = "bio.chloe"
version     = "1.0.0-DEV"
description = "Raven Initiative's custom Discord bot for Star Citizen and more."

plugins {
    // Java.
    id("java")

    // Shadow (https://github.com/GradleUp/shadow/releases).
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    // Maven Central.
    mavenCentral()
}

dependencies {
    // JDA: https://mvnrepository.com/artifact/net.dv8tion/JDA
    implementation("net.dv8tion:JDA:5.2.1") // JDA 5.2.1 (10 November 2024).

    // JSON: https://mvnrepository.com/artifact/org.json/json.
    implementation("org.json:json:20240303") // JSON 20240303 (03 March 2024).

    // SLF4J: https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16") // SLF4J 2.0.16 (10 August 2024).

    // Logback Classic: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.12") // Logback Classic 1.5.12 (25 October 2024).

}

tasks {
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "bio.chloe.Main"
            )
        }
    }
}