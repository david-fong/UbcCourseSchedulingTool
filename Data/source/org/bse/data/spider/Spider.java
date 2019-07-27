package org.bse.data.spider;

import org.bse.data.DataMain;
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

    protected final Document fetchDocument(URI uri) throws IOException {
        return Jsoup.connect(uri.toASCIIString()).get();
    }

    /**
     * TODO: move this. the spider won't use this. Whoever is parsing xml into
     *     objects like courses and requirements will use this.
     *
     * This should not have any package name conflicts since all the packages are
     * anchored with the module name as an included token.
     * @param classObject The class to get the package for. Must be in this module.
     * @return The [File] (directory) object containing [classObject]'s source file.
     */
    protected static File getSourceDir(final Class<?> classObject) {
        final File file = new File(DataMain.RUNTIME_PATH_OF_COMPILED_DATA_MODULE,
                classObject.getPackageName().replace(".", File.separator)
        );
        if (!file.isDirectory()) {
            new ClassNotFoundException(String.format(
                    "package for class %s not found",
                    classObject.getName()
            )).printStackTrace();
        }
        return file;
    }

}
