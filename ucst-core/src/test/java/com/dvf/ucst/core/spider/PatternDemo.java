package com.dvf.ucst.core.spider;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PatternDemo {

    @Test
    void patternDemo() {
        final String testString = "";
    }



    // these will be mutable, and designed for chained setter calls that may make testing code easier to write.
    // these will not be used directly anywhere except as argument to create an immutable version.
    private static final class ThingThatCanBeRowString {
        // todo:
        private int id = -1;
        private String name = null;
        private String status = null;

        private ThingThatCanBeRowString setId(final int id) {
            this.id = id;
            return this;
        }

        private ThingThatCanBeRowString setName(final String name) {
            this.name = name;
            return this;
        }

        private ThingThatCanBeRowString setStatus(final String status) {
            this.status = status;
            return this;
        }
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
        private static final String PRE_TABLE_REGEX;
        private static final String POST_TABLE_REGEX;
        private static final Pattern ROW_STRING_PATTERN;
        private static final Pattern TABLE_CONTAINING_STRING_PATTERN;
        static {
            // this string joining with a list may not always be suitable. if it is, it certainly is pretty (worth it).
            final List<String> groupEnumsInOrder = Stream.of(ID, STATUS, NAME)
                    .map(TableRowPattern::getNamedCapGroup)
                    .collect(Collectors.toList());
            PRE_TABLE_REGEX = ".*";
            POST_TABLE_REGEX = ".*";
            final String rowStringRegex = String.join(",\\s*", groupEnumsInOrder);
            ROW_STRING_PATTERN = Pattern.compile(rowStringRegex);
            TABLE_CONTAINING_STRING_PATTERN = Pattern.compile(PRE_TABLE_REGEX + "\\(" // begin rows regex:
                    + ROW_STRING_PATTERN.pattern() // (same as rowStringRegex)
                    + "\\)*" + POST_TABLE_REGEX // <- close rows regex.
            );
        }

        // constant fields:
        private final String groupName;
        private final String namedCapGroup;

        // the constructor:
        TableRowPattern(final String payloadRegex) {
            this.groupName = name();
            this.namedCapGroup = String.format("(?%s<%s>)", groupName, payloadRegex);
        }

        // what people outside will use:
        public static List<ThingThatCanBeRowString> parseThingsFromRowString(final String stringThatContainsTable) throws Exception {
            // first get rid of everything before and after the table.
            final Matcher tableMatcher = TABLE_CONTAINING_STRING_PATTERN.matcher(stringThatContainsTable);
            if (!tableMatcher.find()) {
                throw new Exception(); // we will create our own exception to use here.
            }
            final String tableString = tableMatcher.group();

            // now, extract information from each row of the table string and collect it.
            final List<ThingThatCanBeRowString> objectsParsedFromRowStrings = new ArrayList<>();
            final Matcher rowMatcher = ROW_STRING_PATTERN.matcher(tableString);
            while (rowMatcher.find()) {
                objectsParsedFromRowStrings.add(new ThingThatCanBeRowString()
                        .setId(Integer.parseInt(rowMatcher.group(ID.namedCapGroup)))
                        .setName(rowMatcher.group(NAME.namedCapGroup))
                        .setStatus(rowMatcher.group(STATUS.namedCapGroup))
                        // you do not need to use the chaining like above.
                        // in fact- if you need to do more work to interpret a string extracted from a
                        // capture group, then you shouldn't purposely try to chain setter calls.
                );
            }
            return objectsParsedFromRowStrings;
        }

        private String getNamedCapGroup() {
            return namedCapGroup;
        }
    }

}
