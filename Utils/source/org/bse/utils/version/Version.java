package org.bse.utils.version;

/**
 * From "Semantic Versioning":
 * MAJOR version when you make incompatible API changes,
 * MINOR version when you add functionality in a backwards compatible manner, and
 * PATCH version when you make backwards compatible bug fixes.
 */
public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;
    private final int patch;

    public Version(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public int compareTo(Version other) {
        if (major > other.major) return 1;
        else if (minor > other.minor) return 1;
        else if (patch > other.patch) return 1;
        else return patch == other.patch ? 0 : -1;
    }

    public final int getMajor() {
        return major;
    }

    public final int getMinor() {
        return minor;
    }

    public final int getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

}
