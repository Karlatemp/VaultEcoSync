
group = "io.github.karlatemp.vault-eco-sync"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        // SpigotMC
        maven(url = "https://hub.spigotmc.org/nexus/content/groups/public")
        // Code MC
        maven(url = "https://repo.codemc.io/repository/maven-public/")
    }
}