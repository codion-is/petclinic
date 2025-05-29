/*
 * This file is part of Codion Petclinic Demo.
 *
 * Codion Petclinic Demo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codion Petclinic Demo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codion Petclinic Demo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2024 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.petclinic.ui;

/**
 * Native image main class wrapper.
 * 
 * This class sets required system properties for native image execution
 * and delegates to the actual application main class.
 */
public final class NativeMain {
    
    public static void main(String[] args) {
        // Set java.home property - required for some Swing components
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            // Fallback for common Linux OpenJDK installation
            javaHome = "/usr/lib/jvm/java-21-openjdk-amd64";
        }
        System.setProperty("java.home", javaHome);
        
        // Ensure AWT/Swing works in native image
        System.setProperty("java.awt.headless", "false");
        
        // Set font path for better font rendering
        System.setProperty("sun.java2d.fontpath", "/usr/share/fonts");
        
        // For H2 database compatibility
        System.setProperty("h2.bindAddress", "localhost");
        
        // Delegate to actual main class
        PetclinicAppPanel.main(args);
    }
}