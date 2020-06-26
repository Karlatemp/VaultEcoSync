plugins {
    java
}

group = "io.github.karlatemp.vault-eco-sync"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation("junit", "junit", "4.12")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:29.0-jre")
    implementation("org.jetbrains:annotations:19.0.0")
    // https://mvnrepository.com/artifact/org.springframework/spring-jdbc
    implementation("com.zaxxer:HikariCP:3.4.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
