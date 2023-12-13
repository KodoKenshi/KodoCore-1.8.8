plugins { kotlin("jvm") version "1.9.21" }

group = "me.kodokenshi"
version = "0.0.0"

repositories { mavenCentral() }

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    compileOnly(files("C:\\Users\\kodokenshi\\Desktop\\KodoKenshi\\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar"))
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
tasks.withType<Jar> {
    archiveFileName = "KodoCore-0.0.0.jar"
    destinationDirectory = file("C:\\Users\\kodokenshi\\.m2\\repository\\me\\kodokenshi\\kodocore1_8_8\\KodoCore\\0.0.0")
}
tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(8) }