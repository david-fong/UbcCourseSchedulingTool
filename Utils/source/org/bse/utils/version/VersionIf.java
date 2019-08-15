package org.bse.utils.version;

/**
 * From "Semantic Versioning":
 * MAJOR version when you make incompatible API changes,
 * MINOR version when you add functionality in a backwards compatible manner, and
 * PATCH version when you make backwards compatible bug fixes.
 */
public interface VersionIf<I extends VersionIf> extends Comparable<I> {

    @Override
    default int compareTo(VersionIf other) {
        if (getMajor() > other.getMajor()) return 1;
        else if (getMinor() > other.getMinor()) return 1;
        else if (getPatch() > other.getPatch()) return 1;
        else return getPatch() == other.getPatch() ? 0 : -1;
    }

    int getMajor();

    int getMinor();

    int getPatch();

    String getDescription();

    default String getVersionString() {
        return String.format("v%d.%d.%d", getMajor(), getMinor(), getPatch());
    }

}
