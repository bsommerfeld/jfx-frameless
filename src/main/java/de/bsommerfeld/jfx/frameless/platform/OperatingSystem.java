package de.bsommerfeld.jfx.frameless.platform;

/**
 * Runtime platform detection.
 * Identifies the current operating system for platform-specific behavior
 * branching.
 */
public enum OperatingSystem {
    WINDOWS,
    MACOS,
    LINUX,
    UNKNOWN;

    private static final OperatingSystem CURRENT;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            CURRENT = WINDOWS;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            CURRENT = MACOS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            CURRENT = LINUX;
        } else {
            CURRENT = UNKNOWN;
        }
    }

    /**
     * Returns the detected operating system at runtime.
     */
    public static OperatingSystem current() {
        return CURRENT;
    }

    public boolean isWindows() {
        return this == WINDOWS;
    }

    public boolean isMacOS() {
        return this == MACOS;
    }

    public boolean isLinux() {
        return this == LINUX;
    }
}
