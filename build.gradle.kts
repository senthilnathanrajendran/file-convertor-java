plugins {
    id("java")
}

group = "com.convertor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx:19")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.opencsv:opencsv:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}