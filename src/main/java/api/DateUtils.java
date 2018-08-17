package api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static void main(String[] args) {
        String fromDateString = "2018-08-01";
        String toDateString = "2018-08-01";

        LocalDate start = LocalDate.parse(fromDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(toDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (LocalDate date = start; date.isBefore(end.plusDays(2)); date = date.plusDays(1)) {
            System.out.println(date.toString());
        }
    }
}