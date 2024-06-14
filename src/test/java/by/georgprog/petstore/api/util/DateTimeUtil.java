package by.georgprog.petstore.api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDateTime toLocalDateTime(String dateTime, String formatter) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(formatter));
    }

    public static LocalDateTime toLocalDateTime(String dateTime, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static String localDateTimeToString(LocalDateTime dateTime, String formatter) {
        return dateTime.format(DateTimeFormatter.ofPattern(formatter));
    }
}
