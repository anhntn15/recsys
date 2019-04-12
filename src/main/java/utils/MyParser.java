package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parser data
 */
public class MyParser {
    private static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.S");
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");

    /**
     * Parse from text to Integer. If don't success return null
     *
     * @param text input String
     * @return a number if success, null otherwise
     */
    public static Integer parseInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse from text to Float. If don't success return null
     *
     * @param text input String
     * @return a number if success, null otherwise
     */
    public static Float parseFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse from text to Double. If don't success return null
     *
     * @param text input String
     * @return a number if success, null otherwise
     */
    public static Double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse from text to Date. If don't success return null
     *
     * @param text input String
     * @return a Date object if success, null otherwise
     */
    public static Date parseDate(String text) {

        try {
            return simpleDateFormat.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     * Parse from text to Date. If don't success return null
     *
     * @param text input String
     * @return a Date object if success, null otherwise
     */
    public static Date parseDateTime(String text) {

        try {
            return simpleDateTimeFormat.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }
}
