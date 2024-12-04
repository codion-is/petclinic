import org.gradle.internal.os.OperatingSystem

plugins {
    id("org.beryx.jlink") version "3.1.1"
    id("com.diffplug.spotless") version "6.25.0"
    id("org.asciidoctor.jvm.convert") version "4.0.3"
}

dependencies {
    implementation(libs.codion.swing.framework.ui)

    implementation(libs.json)
    implementation(libs.flatlaf.intellij.themes)

    runtimeOnly(libs.codion.plugin.logback.proxy)

    runtimeOnly(libs.codion.framework.db.local)
    runtimeOnly(libs.codion.dbms.h2)
    runtimeOnly(libs.h2)

    testImplementation(libs.codion.framework.domain.test)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

version = libs.versions.codion.get().replace("-SNAPSHOT", "")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

spotless {
    java {
        licenseHeaderFile("${rootDir}/license_header").yearSeparator(" - ")
    }
    format("javaMisc") {
        target("src/**/package-info.java", "src/**/module-info.java")
        licenseHeaderFile("${rootDir}/license_header", "\\/\\*\\*").yearSeparator(" - ")
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("codion.db.url", "jdbc:h2:mem:h2db")
    systemProperty("codion.db.initScripts", "classpath:create_schema.sql")
    systemProperty("codion.test.user", "scott:tiger")
}

application {
    mainModule = "is.codion.demos.petclinic"
    mainClass = "is.codion.demos.petclinic.ui.PetclinicAppPanel"
    applicationDefaultJvmArgs = listOf(
        "-Xmx64m",
        "-Dcodion.client.connectionType=local",
        "-Dcodion.db.url=jdbc:h2:mem:h2db",
        "-Dcodion.db.initScripts=classpath:create_schema.sql",
        "-Dsun.awt.disablegrab=true"
    )
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isDeprecation = true
}

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

tasks.register<WriteProperties>("writeVersion") {
    destinationFile = file("${temporaryDir.absolutePath}/version.properties")
    property("version", libs.versions.codion.get().replace("-SNAPSHOT", ""))
}

tasks.processResources {
    from(tasks.named("writeVersion"))
}

tasks.register<Sync>("copyToGitHubPages") {
    group = "documentation"
    from(tasks.asciidoctor)
    into("../codion-pages/doc/" + project.version + "/tutorials/petclinic")
}

jlink {
    imageName = project.name
    options = listOf(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--add-modules",
        "is.codion.framework.db.local,is.codion.dbms.h2,is.codion.plugin.logback.proxy"
    )

    addExtraDependencies("slf4j-api")

    jpackage {
        imageName = "Petclinic"
        if (OperatingSystem.current().isLinux) {
            installerType = "deb"
            icon = "src/main/icons/petclinic.png"
            installerOptions = listOf(
                "--resource-dir",
                "build/jpackage/Petclinic/lib",
                "--linux-shortcut"
            )
        }
        if (OperatingSystem.current().isWindows) {
            installerType = "msi"
            icon = "src/main/icons/petclinic.ico"
            installerOptions = listOf(
                "--win-menu",
                "--win-shortcut"
            )
        }
    }
}
