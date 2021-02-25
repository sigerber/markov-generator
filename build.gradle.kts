import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

val kotlinLanguageVersion: String by project
val koinVersion: String by project
val inlineLoggerVersion: String by project
val logbackVersion: String by project
val spekVersion: String by project
val kotestVersion: String by project
val junitJupiterVersion: String by project

plugins {
    base
    jacoco
    kotlin("jvm")
}

apply {
    from("./jacoco.gradle.kts")
}

allprojects {
    group = "com.prospection.markov.generator"
    version = "1.0"

    repositories {
        mavenLocal()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://dl.bintray.com/kotlin/ktor")
        maven(url = "https://dl.bintray.com/spekframework/spek-dev")
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    tasks.withType<KotlinCompile<*>> {
        kotlinOptions {
            languageVersion = kotlinLanguageVersion
            apiVersion = kotlinLanguageVersion
            (this as org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions).jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOfNotNull(
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()

        systemProperty("pact.provider.version", System.getProperty("pact.provider.version", project.version.toString()))
        systemProperty("pact.verifier.publishResults", System.getProperty("pact.verifier.publishResults", "false"))
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:$inlineLoggerVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("org.koin:koin-core:$koinVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    }
}
