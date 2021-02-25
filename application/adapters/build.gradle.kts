val ktorVersion: String by rootProject
val koinVersion: String by rootProject

dependencies {
    implementation(project(":application:ports"))
    implementation(project(":application:shared"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("net.pwall.json:json-kotlin:4.0")
    implementation("net.pwall.json:json-ktor:1.2")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("net.pwall.json:json-ktor-client:0.6")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("au.com.dius.pact.provider:junit5:4.1.0")
}
