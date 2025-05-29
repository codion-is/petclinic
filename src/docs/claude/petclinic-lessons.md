# PetClinic GraalVM Native Success Story

## Summary

Successfully converted Codion PetClinic demo to GraalVM native image - a complex desktop application with Swing UI, H2 database, and full CRUD functionality.

## Critical Success Factors

### 1. Comprehensive Tracing Agent Session

**THE GAME CHANGER**: The difference between failure and success was the thoroughness of the tracing agent run.

**Failed Attempts**: 
- Quick 5-15 second agent runs
- Partial UI testing
- Result: AWT library loading crashes (`Fatal error reported via JNI: Could not allocate library name`)

**Successful Approach**:
- Extended comprehensive testing session
- Exercised ALL features: forms, navigation, CRUD operations, validation, themes
- Result: Metadata tripled (66→237 JNI types), native image works perfectly

### 2. Shadow JAR for Agent Tracing

**Critical**: Must use shadow JAR for agent tracing to include all dependencies:
```bash
./gradlew shadowJar
java -agentlib:native-image-agent=config-output-dir=agent-config -jar build/libs/petclinic-0.18.35-all.jar
```

### 3. Proper Metadata Integration

Copy the comprehensive metadata to native image config:
```bash
cp agent-config/reachability-metadata.json src/main/resources/META-INF/native-image/is.codion.demos.petclinic/
```

## Build Configuration That Worked

### build.gradle.kts additions:
```kotlin
plugins {
    id("org.graalvm.buildtools.native") version "0.10.4"
}

graalvmNative {
    binaries {
        named("main") {
            imageName = project.name
            mainClass = "is.codion.demos.petclinic.ui.NativeMain"
            buildArgs.addAll(
                "-H:+ForeignAPISupport",
                "-H:+ReportExceptionStackTraces",
                "-H:+UnlockExperimentalVMOptions"
            )
        }
    }
    
    agent {
        defaultMode = "standard"
    }
    
    metadataRepository {
        enabled = true
    }
}
```

### gradle.properties:
```properties
org.gradle.java.home=/home/darri/.sdkman/candidates/java/24.0.1-graal
```

### native-image.properties:
```properties
Args = --no-fallback \
       -Djava.awt.headless=false \
       --initialize-at-run-time=sun.awt,com.sun.jna,sun.java2d,sun.font,java.awt.Toolkit,sun.awt.AWTAccessor,javax.swing,java.awt.Component,java.awt.Container,javax.swing.JComponent \
       -H:+UnlockExperimentalVMOptions \
       -H:+ReportExceptionStackTraces \
       -H:+AddAllCharsets \
       -H:+IncludeAllLocales \
       -H:+EnableAllSecurityServices \
       -H:+JNI \
       -H:+ForeignAPISupport \
       -H:ConfigurationFileDirectories=META-INF/native-image \
       --enable-url-protocols=http,https \
       --add-modules=ALL-MODULE-PATH
```

### NativeMain.java wrapper:
```java
public final class NativeMain {
    public static void main(String[] args) {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = "/usr/lib/jvm/java-21-openjdk-amd64";
        }
        System.setProperty("java.home", javaHome);
        System.setProperty("java.awt.headless", "false");
        System.setProperty("sun.java2d.fontpath", "/usr/share/fonts");
        System.setProperty("h2.bindAddress", "localhost");
        
        PetclinicAppPanel.main(args);
    }
}
```

## Final Results

### Build Metrics:
- **Build time**: 1m 59s
- **Binary size**: 158.77MB (includes everything)
- **Reachable types**: 19,956 (vs 16,441 without comprehensive tracing)
- **Reflection metadata**: 51,767 types, 223 fields, 2,844 methods
- **JNI metadata**: 237 types, 218 fields, 125 methods (3.5x increase!)

### Runtime Performance:
- **Startup**: Instant (milliseconds vs 2-3 seconds)
- **Memory**: Significantly reduced vs JVM
- **Functionality**: 100% working - all CRUD, UI, database operations
- **Deployment**: Single binary, no JVM required

### Libraries Included:
- libawt.so, libawt_headless.so, libawt_xawt.so
- libfontmanager.so, libjavajpeg.so, libjawt.so
- libjsound.so (captured during comprehensive testing!)
- libjvm.so, liblcms.so

## Key Takeaways for Complex Apps

1. **Never skip comprehensive agent tracing** - this is THE critical step
2. **Test every single feature** during agent run - UI, database, validation, errors
3. **Use shadow JAR** for agent tracing to include all dependencies
4. **Expect larger binaries** for complex desktop apps (~160MB vs ~130MB for simpler apps)
5. **AWT/Swing issues resolve** with proper metadata capture
6. **Database integration works** perfectly when properly traced

## Template for Next App

This exact approach should work for any Codion desktop application:
1. Add GraalVM plugin and configuration
2. Create NativeMain wrapper
3. Build shadow JAR
4. Run comprehensive agent tracing session
5. Copy metadata and rebuild
6. Test native executable

The PetClinic success proves that complex Codion desktop applications with databases and rich UIs can be successfully converted to GraalVM native images with the right approach.