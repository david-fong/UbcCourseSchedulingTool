package com.dvf.ucst.utils.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Allows simple calendar data to be stored in .csv format and imported to Google
 * Calendars. See the below link for the spec.
 * https://support.google.com/calendar/answer/37118?hl=en#format_csv
 */
public enum GoogleCalCsvColumns {
    SUBJECT ("Subject"), // FACULTY COURSE_ID SECTION_ID
    START_DATE ("Start Date"), // MM/DD/YYYY
    END_DATE ("End Date"),
    START_TIME ("Start Time"), // HH:MM (AM|PM)
    END_TIME ("End Time"), // HH:MM (AM|PM)
    ALL_DAY_EVENT ("All Day Event"),
    DESCRIPTION ("Description"),
    LOCATION ("Location"),
    PRIVATE ("Private"),
    ;
    private final String name;

    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm a");

    GoogleCalCsvColumns(String name) {
        this.name = String.format("\"%s\"", name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @param headings The [GoogleCalCsvColumns] columns to include and in what order.
     * @param rows A [List] of rows that each map a [GoogleCalCsvColumns] to a value.
     * @return A String that Google Calendars can interpret as a calendar.
     */
    public static String getCalendarString(final List<GoogleCalCsvColumns> headings,
                                           final List<EnumMap<GoogleCalCsvColumns, String>> rows) {
        final StringBuilder csv = new StringBuilder(); {
            // Initialize output with specified table header in given column order:
            final StringJoiner headerRow = new StringJoiner(",", "", "\n");
            for (final GoogleCalCsvColumns heading : headings) {
                headerRow.add(heading.name);
            }
            csv.append(headerRow.toString());
        }
        for (final Map<GoogleCalCsvColumns, String> row : rows) {
            final StringJoiner rowString = new StringJoiner(",", "", "\n");
            // Add each row, placing values in the order of the table header columns:
            for (final GoogleCalCsvColumns heading : headings) {
                rowString.add(cleanColumnValue(row.get(heading)));
            }
            csv.append(rowString.toString());
        }
        return csv.toString();
    }

    /**
     * @param columnValue A String to use as a column value in the .csv String.
     * @return A version of [columnValue] that is safe to use in a csv file.
     */
    private static String cleanColumnValue(final String columnValue) {
        return String.format("\"%s\"", columnValue.replaceAll("\"", "\\\""));
    }

}
