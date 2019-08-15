package org.bse.core;

import org.bse.utils.version.VersionIf;

/**
 *
 */
public enum UCSToolVersion implements VersionIf<UCSToolVersion> {
    VERSION_0_0_0("")
    ;
    private final int major;
    private final int minor;
    private final int patch;
    private final String description;

    UCSToolVersion(String description) {
        this.major = Integer.parseInt(name().split("_")[3]);
        this.minor = Integer.parseInt(name().split("_")[2]);
        this.patch = Integer.parseInt(name().split("_")[1]);
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

    public static UCSToolVersion getLatestVersion() {
        return values()[values().length - 1];
    }
}
