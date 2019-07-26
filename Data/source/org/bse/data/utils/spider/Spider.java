package org.bse.data.utils.spider;

import org.bse.data.DataMain;
import org.bse.data.faculties.vancouver.VancouverFaculties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Traverses the web gathering information on course offerings at UBC, and outputting
 * java files representing those courses.
 */
public abstract class Spider {

    public static final File VAN_FACULTIES_DIR = getSourceDir(VancouverFaculties.class);

    protected final Document fetchDocument(URI uri) throws IOException {
        return Jsoup.connect(uri.toASCIIString()).get();
    }

    /**
     * This should not have any package name conflicts since all the packages are
     * anchored with the module name as an included token.
     * @param classObject The class to get the package for. Must be in this module.
     * @return The [File] (directory) object containing [classObject]'s source file.
     */
    protected static File getSourceDir(final Class<?> classObject) {
        // TODO: change this to use what I read on stack overflow.
        final File file = new File(DataMain.MODULE_SOURCES_DIR,
                classObject.getPackageName().replace(".", File.separator)
        );
        assert file.isDirectory() : "package directory not found";
        return file;
    }

}
