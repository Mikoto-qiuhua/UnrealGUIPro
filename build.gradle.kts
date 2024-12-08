plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.qiuhua.UnrealGUIPro"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://jitpack.io")
    maven {
        name = "spigotmc-repo"
        url = uri ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }  //SpigotMC仓库
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
    compileOnly(fileTree("src/libs"))
}






tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}

tasks.withType<Jar>().configureEach {
    archiveFileName.set("UnrealGUIPro.jar")
    destinationDirectory.set(File ("E:\\我的世界插件"))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    manifest {}
    relocate("org.openjdk.nashorn", "org.qiuhua.nashorn")
    dependencies {
        include(dependency("org.openjdk.nashorn:nashorn-core:15.4"))
    }
}

