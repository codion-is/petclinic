import org.gradle.internal.os.OperatingSystem

plugins {
    // The Badass Jlink Plugin provides jlink and jpackage
    // functionality and applies the java application plugin
    // https://badass-jlink-plugin.beryx.org
    id("org.beryx.jlink") version "3.1.1"
    // Just for managing the license headers
    id("com.diffplug.spotless") version "7.0.1"
    // For the asciidoctor docs
    id("org.asciidoctor.jvm.convert") version "4.0.4"
    // For GitHub Releases
    id("com.github.breadmoirai.github-release") version "2.5.2"
}

dependencies {
    // Import the Codion BOM for dependency version management
    implementation(platform(libs.codion.framework.bom))

    // The Codion framework UI module, transitively pulls in all required
    // modules, such as the model layer and the core database module
    implementation(libs.codion.swing.framework.ui)
    // Include all the standard Flat Look and Feels and a bunch of IntelliJ
    // theme based ones, available via the View -> Select Look & Feel menu
    implementation(libs.codion.plugin.flatlaf)
    implementation(libs.codion.plugin.flatlaf.intellij.themes)

    // Provides the Logback logging library as a transitive dependency
    // and provides logging configuration via the Help -> Log menu
    runtimeOnly(libs.codion.plugin.logback.proxy)
    // Provides the local JDBC connection implementation
    runtimeOnly(libs.codion.framework.db.local)
    // The H2 database implementation
    runtimeOnly(libs.codion.dbms.h2)
    // And the H2 database driver
    runtimeOnly(libs.h2)

    // The domain model unit test module
    testImplementation(libs.codion.framework.domain.test)
    testImplementation(libs.codion.framework.db.local)
}

// The application version simply follows the Codion framework version used
version = libs.versions.codion.get().replace("-SNAPSHOT", "")

java {
    toolchain {
        // Use the latest possible Java version
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

spotless {
    // Just the license headers
    java {
        licenseHeaderFile("${rootDir}/license_header").yearSeparator(" - ")
    }
    format("javaMisc") {
        target("src/**/package-info.java", "src/**/module-info.java")
        licenseHeaderFile("${rootDir}/license_header", "\\/\\*\\*").yearSeparator(" - ")
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            targets {
                all {
                    // System properties required for running the unit tests
                    testTask.configure {
                        // The JDBC url
                        systemProperty("codion.db.url", "jdbc:h2:mem:h2db")
                        // The database initialization script
                        systemProperty("codion.db.initScripts", "classpath:create_schema.sql")
                        // The user to use when running the tests
                        systemProperty("codion.test.user", "scott:tiger")
                    }
                }
            }
        }
    }
}

// Configure the application plugin, the jlink plugin relies
// on this configuration when building the runtime image
application {
    mainModule = "is.codion.demos.petclinic"
    mainClass = "is.codion.demos.petclinic.ui.PetclinicAppPanel"
    applicationDefaultJvmArgs = listOf(
        // This app doesn't require a lot of memory
        "-Xmx64m",
        // Specify a local JDBC connection
        "-Dcodion.client.connectionType=local",
        // The JDBC url
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        // The database initialization script
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        // Just in case we're debugging in Linux, nevermind
        "-Dsun.awt.disablegrab=true"
    )
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isDeprecation = true
}

// Configure the docs generation
tasks.asciidoctor {
    inputs.dir("src")
    baseDirFollowsSourceFile()
    attributes(
        mapOf(
            "codion-version" to project.version,
            "source-highlighter" to "prettify",
            "tabsize" to "2"
        )
    )
    asciidoctorj {
        setVersion("2.5.13")
    }
}

// Create a version.properties file containing the application version
tasks.register<WriteProperties>("writeVersion") {
    destinationFile = file("${temporaryDir.absolutePath}/version.properties")
    property("version", libs.versions.codion.get().replace("-SNAPSHOT", ""))
}

// Include the version.properties file from above in the
// application resources, see usage in PetclinicAppModel
tasks.processResources {
    from(tasks.named("writeVersion"))
}

// Configure the Jlink plugin
jlink {
    // Specify the jlink image name
    imageName = project.name + "-" + project.version + "-" +
            OperatingSystem.current().familyName.replace(" ", "").lowercase()
    // The options for the jlink task
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        // Add the modular runtimeOnly dependencies, which are handled by the ServiceLoader.
        // These don't have an associated 'requires' clause in module-info.java
        // and are therefore not added automatically by the jlink plugin.
        "--add-modules",
        // The local JDBC connection implementation
        "is.codion.framework.db.local," +
                // The H2 database implementation
                "is.codion.dbms.h2," +
                // The Logback plugin
                "is.codion.plugin.logback.proxy"
    )

    // H2 database uses slf4j, but is non-modular so the jlink plugin,
    // can't derive that dependency, so here we help it along.
    addExtraDependencies("slf4j-api")

    jpackage {
        if (OperatingSystem.current().isLinux) {
            icon = "src/main/icons/petclinic.png"
            installerType = "deb"
            installerOptions = listOf(
                "--linux-shortcut"
            )
        }
        if (OperatingSystem.current().isWindows) {
            icon = "src/main/icons/petclinic.ico"
            installerType = "msi"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
        }
        if (OperatingSystem.current().isMacOsX) {
            icon = "src/main/icons/petclinic.icns"
            installerType = "dmg"
        }
    }
}

if (properties.containsKey("githubAccessToken")) {
    githubRelease {
        token(properties["githubAccessToken"] as String)
        owner = "codion-is"
        allowUploadToExisting = true
        releaseAssets.from(tasks.named("jlinkZip").get().outputs.files)
        releaseAssets.from(fileTree(tasks.named("jpackage").get().outputs.files.singleFile) {
            exclude(project.name + "/**", project.name + ".app/**")
        })
    }
}

tasks.named("githubRelease") {
    dependsOn(tasks.named("jlinkZip"))
    dependsOn(tasks.named("jpackage"))
}

// Copies the documentation to the Codion github pages repository, nevermind
tasks.register<Sync>("copyToGitHubPages") {
    group = "documentation"
    from(tasks.asciidoctor)
    into("../codion-pages/doc/" + project.version + "/tutorials/petclinic")
}