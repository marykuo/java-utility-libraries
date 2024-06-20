package com.marykuo.demo.time;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

@Slf4j
public class DateTimeUtils {

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final DateTimeFormatter inputStrictFormatter =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("[uuuuMMdd]") // 可接受解析的格式
                    .toFormatter(Locale.getDefault()) // 預設時區
                    .withChronology(IsoChronology.INSTANCE) // ISO 日期格式
                    .withResolverStyle(ResolverStyle.STRICT); // 嚴謹解析，例如 02/29 不是每年都有

    private static final DateTimeFormatter OUTPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * @return current system clock
     */
    public static Clock current() {
        return Clock.systemDefaultZone();
    }

    /**
     * @return milliseconds from the epoch of 1970-01-01T00:00:00Z (UTC+0)
     */
    public static Timestamp getCurrentTimestamp() {
        return Timestamp.from(current().instant());
    }

    /**
     * @return current system date
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now(current());
    }

    /**
     * @return current system time
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(current());
    }

    /**
     * @return current system date in yyyyMMdd format
     */
    public static String getCurrentDate() {
        return formatToYYYYMMDD(getCurrentLocalDate());
    }

    /**
     * @return current system time in HH:mm:ss format
     */
    public static String getCurrentTime() {
        return formatToString(LocalTime.now(current()));
    }

    // ==================== Transfer ====================
    // transfer between the following types:
    // 1. LocalDateTime (LocalDate): main type in the system and database
    // 2. Long (Timestamp): main type in the API and front-end
    // 3. Date String: main type in log or message

    /**
     * @return timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        assert localDateTime != null;
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * @return timestamp
     */
    public static Timestamp toTimestamp(LocalDate localDate) {
        assert localDate != null;
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    /**
     * @return localDateTime in system default zone
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        assert timestamp != null;
        try {
            return Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            throw new RuntimeException("date transfer error");
        }
    }

    /**
     * @param dateStr only accept yyyyMMdd format
     * @return localDate
     */
    public static LocalDate toLocalDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, inputStrictFormatter);
        } catch (Exception e) {
            throw new RuntimeException("date transfer error");
        }
    }

    /**
     * @return yyyy-MM-dd
     */
    public static String formatToDashedYYYYMMDD(LocalDate localDate) {
        return localDate == null ? "" : localDate.format(DateTimeFormatter.ISO_DATE);
    }

    /**
     * @return yyyyMMdd
     */
    public static String formatToYYYYMMDD(LocalDate localDate) {
        return localDate == null ? "" : localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     * @return HH:mm:ss
     */
    public static String formatToString(LocalTime localTime) {
        return localTime == null ? "" : localTime.format(OUTPUT_TIME_FORMATTER);
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatToString(LocalDateTime localDateTime) {
        return localDateTime == null ? "" : formatToDashedYYYYMMDD(localDateTime.toLocalDate()) + " " + formatToString(localDateTime.toLocalTime());
    }

    // ==================== Validation ====================

    /**
     * @param dateStr only accept yyyyMMdd format
     * @return
     */
    public static boolean isValidDate(String dateStr) {
        try {
            final LocalDate localDate = toLocalDate(dateStr);
            return localDate.getYear() >= 2000;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param timeStr only accept HH:mm:ss format
     * @return true if valid
     */
    public static boolean isValidTime(String timeStr) {
        assert timeStr != null;
        return timeStr.matches("^([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$");
    }

    // ==================== Comparison ====================

    /**
     * target 日期是否在 (reference - pastDays) 和 (reference + futureDays) 之間 (包含等於)
     * <pre>
     *     isBetween("2023-01-08", "2023-01-10", 1, 1) = false
     *     isBetween("2023-01-09", "2023-01-10", 1, 1) = true
     *     isBetween("2023-01-10", "2023-01-10", 1, 1) = true
     *     isBetween("2023-01-11", "2023-01-10", 1, 1) = true
     *     isBetween("2023-01-12", "2023-01-10", 1, 1) = false
     * </pre>
     */
    public static boolean isBetween(LocalDate target, LocalDate reference, int pastDays, int futureDays) {
        assert target != null;
        assert reference != null;
        assert pastDays >= 0;
        assert futureDays >= 0;
        return isBetween(target, reference.minusDays(pastDays), reference.plusDays(futureDays));
    }

    /**
     * target 日期是否在 past 和 future 之間 (包含等於)
     * <pre>
     *     isBetween("2023-01-08", "2023-01-09", "2023-01-11") = false
     *     isBetween("2023-01-09", "2023-01-09", "2023-01-11") = true
     *     isBetween("2023-01-10", "2023-01-09", "2023-01-11") = true
     *     isBetween("2023-01-11", "2023-01-09", "2023-01-11") = true
     *     isBetween("2023-01-12", "2023-01-09", "2023-01-11") = false
     * </pre>
     */
    public static boolean isBetween(LocalDate target, LocalDate past, LocalDate future) {
        assert target != null;
        assert past != null;
        assert future != null;
        return isBeforeEquals(target, future) && isAfterEquals(target, past);
    }

    /**
     * target 日期是否早於或等於 reference
     * <pre>
     *     isBeforeEquals("2023-01-01", "2023-01-02") = true
     *     isBeforeEquals("2023-01-02", "2023-01-02") = true
     *     isBeforeEquals("2023-01-03", "2023-01-02") = false
     * </pre>
     */
    public static boolean isBeforeEquals(LocalDate target, LocalDate reference) {
        assert target != null;
        assert reference != null;
        return target.isBefore(reference) || target.isEqual(reference);
    }

    /**
     * target 日期是否晚於或等於 reference
     * <pre>
     *     isAfterEquals("2023-01-01", "2023-01-02") = false
     *     isAfterEquals("2023-01-02", "2023-01-02") = true
     *     isAfterEquals("2023-01-03", "2023-01-02") = true
     * </pre>
     */
    public static boolean isAfterEquals(LocalDate target, LocalDate reference) {
        assert target != null;
        assert reference != null;
        return target.isAfter(reference) || target.isEqual(reference);
    }
}
