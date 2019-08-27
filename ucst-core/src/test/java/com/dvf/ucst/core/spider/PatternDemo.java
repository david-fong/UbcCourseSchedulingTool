package com.dvf.ucst.core.spider;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternDemo {

    @Test
    void patternDemo() {
        final String testString = "";
    }



    // whatever class you're trying to create from a string might be a non-static inner-class
    private /*static*/ final class ThingThatCanBeRowString {
        // todo:
        private final int id = -1;
        private final String name = null;
        private final String status = null;
    }

    // written for a very specific use-case (hence "private" visibility).
    private enum TableRowPattern {
        ID ("\\d+"),
        NAME ("\\p{Alpha}*"),
        STATUS ("[^,]*"),
        ;

        /*
        pattern that matches a String containing a table String.
         */
        private static final Pattern TABLE_CONTAINING_STRING_PATTERN;
        static {
            // this string joining with a list may not always be suitable. if it is, it certainly is pretty (worth it).
            final List<String> groupEnumsInOrder = Stream.of(ID, STATUS, NAME)
                    .map(TableRowPattern::getNamedCapGroup)
                    .collect(Collectors.toList());
            TABLE_CONTAINING_STRING_PATTERN = Pattern.compile(".*\\(" // begin rows regex:
                    + String.join(",\\s*", groupEnumsInOrder)
                    + "\\)*.*" // <- close rows regex.
            );
        }

        // constant fields:
        private final String groupName;
        private final String namedCapGroup;

        TableRowPattern(final String payloadRegex) {
            this.groupName = name();
            this.namedCapGroup = String.format("(?%s<%s>)", groupName, payloadRegex);
        }

        public static List<ThingThatCanBeRowString> parseThingsFromRowString(final String tableContainingString) {
            return null; // TODO:
        }

        private String getNamedCapGroup() {
            return namedCapGroup;
        }
    }

}
