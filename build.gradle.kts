import org.gradle.api.JavaVersion

plugins {
  java
  `java-library`

  `maven-publish`

  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0"

  id("io.github.goooler.shadow") version "8.1.7"
}

val groupStringSeparator = "."
val kebabcaseStringSeparator = "-"
val snakecaseStringSeparator = "_"

fun capitalizeFirstLetter(string: String): String {
  return string.first().uppercase() + string.slice(IntRange(1, string.length - 1))
}

fun snakecase(kebabcaseString: String): String {
  return kebabcaseString.lowercase().replace(kebabcaseStringSeparator, snakecaseStringSeparator)
}

fun pascalcase(kebabcaseString: String): String {
  var pascalCaseString = ""

  val splitString = kebabcaseString.split(kebabcaseStringSeparator)

  for (part in splitString) {
    pascalCaseString += capitalizeFirstLetter(part)
  }

  return pascalCaseString
}

description = "A simple rank system project. Saves ranks to a yml file, supports prefixes and permissions as well as chat and tab list prefixes. Tab list players are sorted based on their rank."

val mainProjectAuthor = "Esoteric Enderman"
val projectAuthors = listOfNotNull(mainProjectAuthor)

val topLevelDomain = "dev"

val projectNameString = rootProject.name

group = topLevelDomain + groupStringSeparator + mainProjectAuthor.lowercase() + groupStringSeparator + snakecase(rootProject.name)
version = "1.0.0-SNAPSHOT"

val projectGroupString = group.toString()
val projectVersionString = version.toString()

val javaVersion = 21
val javaVersionEnumMember = JavaVersion.VERSION_21;
val paperApiVersion = "1.21"

java {
  sourceCompatibility = javaVersionEnumMember
  targetCompatibility = javaVersionEnumMember

  toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

repositories {
    mavenCentral()
}

dependencies {
  paperweight.paperDevBundle("$paperApiVersion-R0.1-SNAPSHOT")
}

tasks {
  build {
    dependsOn(shadowJar)
  }

  compileJava {
    options.release = javaVersion
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = projectGroupString
            artifactId = projectNameString
            version = projectVersionString

            fun artifactPath(classifier: String): String {
              return "${layout.buildDirectory}/libs/" + projectNameString + "-" + projectVersionString + "-" + classifier + ".jar"
            }

            val devAllClassifier = "dev-all"
            val devClassifier = "dev"

            artifact(artifactPath(devAllClassifier)) {
                classifier = devAllClassifier
            }

            artifact(artifactPath(devClassifier)) {
                classifier = devClassifier
            }
        }
    }
}

tasks.named("publishMavenJavaPublicationToMavenLocal") {
  dependsOn(tasks.named("build"))
}
