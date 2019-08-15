package org.bse.core;

import org.bse.utils.version.VersionIf;

/**
 *
 */
public enum Version implements VersionIf<Version> {
    VERSION_0_0_0 (0, 0, 0, "")
    ;
    private final int major;
    private final int minor;
    private final int patch;
    private final String description;

    Version(int major, int minor, int patch, String description) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.description = description;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getPatch() {
        return patch;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getVersionString();
    }
}
