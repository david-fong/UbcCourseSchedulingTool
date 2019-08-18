package com.dvf.ucst.core;

import com.dvf.ucst.utils.version.VersionIf;

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
        final String[] tokens = name().split("_");
        this.major = Integer.parseInt(tokens[tokens.length - 1]);
        this.minor = Integer.parseInt(tokens[tokens.length - 2]);
        this.patch = Integer.parseInt(tokens[tokens.length - 3]);
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
