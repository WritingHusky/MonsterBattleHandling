plugins {
    id("java")
}

group = "org.MonsterBattler"
version = "2.5"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.json:json:20231013")
    implementation("commons-io:commons-io:2.16.1")
    testImplementation ("org.mockito:mockito-core:3.12.4")
}

tasks.test {
    useJUnitPlatform()
}