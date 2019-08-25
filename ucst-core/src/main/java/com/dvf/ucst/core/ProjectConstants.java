package com.dvf.ucst.core;

import com.dvf.ucst.utils.version.VersionIf;

/**
 *
 */
public final class ProjectConstants {

    public static final String PROJECT_NAME;
    public static final String REPO_URL;
    public static final UCSToolVersion CURRENTLY_RUNNING_VERSION;
    public static final String TEAM_NAME;
    static {
        PROJECT_NAME = "UBC Course Scheduling Tool";
        REPO_URL = "https://github.com/david-fong/UbcCourseSchedulingTool";
        CURRENTLY_RUNNING_VERSION = UCSToolVersion.values()[UCSToolVersion.values().length - 1];
        TEAM_NAME = "The UCST Team";
    }

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
    }

}
