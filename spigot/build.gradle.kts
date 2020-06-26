plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "io.github.karlatemp.vault-eco-sync"
version = "1.2-SNAPSHOT"

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation(project(":common"))
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:29.0-jre")
    implementation("org.jetbrains:annotations:19.0.0")
    // https://mvnrepository.com/artifact/org.springframework/spring-jdbc
    implementation("com.zaxxer:HikariCP:3.4.5")
}

buildscript {
    repositories {
        maven("https://gitee.com/Karlatemp/Karlatemp-repo/raw/master/")
    }
    dependencies.classpath("io.github.karlatemp:Java8Converter:1.0.3")
}

io.github.karlatemp.java8converter.Java8Converter().apply(project)

tasks.named("java8converter", io.github.karlatemp.java8converter.ConverterTask::class.java).configure {
    dependsOn("shadowJar")
    setup {
        scanner {
            listOf(File(project.buildDir, "libs/VaultEcoSync-${project.version}.jar"))
        }
        filter {
            name.startsWith("io/github/karlatemp/")
        }
        resource {
            name.endsWith(".yml")
        }
        "version" property project.version
    }
}

tasks.withType(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    this.archiveFileName.set("VaultEcoSync-${project.version}.jar")
    dependencies {
        exclude {
            when ("${it.moduleGroup}:${it.moduleName}") {

                else -> {
                    println("${it.moduleGroup} ${it.moduleName} ${it.moduleVersion}")
                    false
                }
            }
        }
    }
}

tasks.named("jar").get().enabled = false

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
