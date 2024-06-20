package com.marykuo.demo.time;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Timestamp;
import java.time.*;

import static com.marykuo.demo.time.DateTimeUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class DateTimeUtilsTest {

    @Test
    public void current_Test() {
        // given: UTC+0
        final String instant = "2024-01-02T03:04:05Z"; // UTC+0

        // when: UTC+8
        final Clock fixedClock = Clock.fixed(Instant.parse(instant), ZoneId.of("Asia/Taipei"));
        try (MockedStatic<Clock> mockedStatic = mockStatic(Clock.class)) {
            mockedStatic.when(Clock::systemDefaultZone).thenReturn(fixedClock);

            // then
            assertThat(current()).isEqualTo(fixedClock);
            assertThat(getCurrentDate()).isEqualTo("20240102");
            assertThat(getCurrentTime()).isEqualTo("11:04:05");
        }
    }

    // ==================== Transfer ====================

    @Test
    public void localDateTimeToTimestamp() {
        // when
        final LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        final Timestamp expected = Timestamp.valueOf("2024-01-02 03:04:05");

        // then
        assertThat(toTimestamp(localDateTime)).isEqualTo(expected);
    }

    @Test
    public void localDateToTimestamp() {
        // when
        final LocalDate localDate = LocalDate.of(2024, 1, 1);
        final Timestamp expected = Timestamp.valueOf("2024-01-01 00:00:00");

        // then
        assertThat(toTimestamp(localDate)).isEqualTo(expected);
    }

    @Test
    public void longToLocalDateTime_Test() {
        // when
        final Long timestamp = Timestamp.valueOf("2024-01-02 03:04:05").getTime();
        final LocalDateTime expected = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

        // then
        assertThat(toLocalDateTime(timestamp)).isEqualTo(expected);
    }

    @Test
    public void stringToLocalDate_Test() {
        // when
        final String date = "20240101";
        final LocalDate expected = LocalDate.of(2024, 1, 1);

        // then
        assertThat(toLocalDate(date)).isEqualTo(expected);
    }

    @Test
    public void formatToDashedYYYYMMDD_Test() {
        // when
        final LocalDate localDate = LocalDate.of(2024, 1, 1);

        // then
        assertThat(formatToDashedYYYYMMDD(localDate)).isEqualTo("2024-01-01");
    }

    @Test
    public void formatToYYYYMMDD_Test() {
        // when
        final LocalDate localDate = LocalDate.of(2024, 1, 1);

        // then
        assertThat(formatToYYYYMMDD(localDate)).isEqualTo("20240101");
    }

    @Test
    public void formatLocalTimeToString_Test() {
        // when
        final LocalTime localTime = LocalTime.of(1, 2, 3);

        // then
        assertThat(formatToString(localTime)).isEqualTo("01:02:03");
    }

    @Test
    public void formatLocalDateTimeToString_Test() {
        // when
        final LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

        // then
        assertThat(formatToString(localDateTime)).isEqualTo("2024-01-02 03:04:05");
    }

    // ==================== Validation ====================

    @Test
    public void isValidDate_Test() {
        // 西元年
        assertThat(isValidDate("20240101")).isTrue();
        assertThat(isValidDate("20240132")).isFalse();
        assertThat(isValidDate("20230229")).isFalse();
        assertThat(isValidDate("20241301")).isFalse();
        assertThat(isValidDate("2024-01-01")).isFalse();

        // 民國年
        assertThat(isValidDate("1130101")).isFalse();
        assertThat(isValidDate("01130101")).isFalse();
    }

    @Test
    public void isValidTime_Test() {
        assertThat(isValidTime("00:00:00")).isTrue();
        assertThat(isValidTime("23:59:59")).isTrue();
        assertThat(isValidTime("24:00:00")).isFalse();
        assertThat(isValidTime("23:60:00")).isFalse();
        assertThat(isValidTime("23:59:60")).isFalse();

        assertThat(isValidTime("23:59")).isFalse();
        assertThat(isValidTime("23:59:59.001")).isFalse();
        assertThat(isValidTime("23:59:59.999999999")).isFalse();
    }

    // ==================== Comparison ====================

    @Test
    public void isBetween_byReference_Test() {
        // given
        final LocalDate reference = LocalDate.of(2024, 1, 10);
        final int pastDays = 1;
        final int futureDays = 1;

        // when & then
        assertThat(isBetween(LocalDate.of(2024, 1, 8), reference, pastDays, futureDays)).isFalse();
        assertThat(isBetween(LocalDate.of(2024, 1, 9), reference, pastDays, futureDays)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 10), reference, pastDays, futureDays)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 11), reference, pastDays, futureDays)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 12), reference, pastDays, futureDays)).isFalse();
    }

    @Test
    public void isBetween_Test() {
        // given
        final LocalDate past = LocalDate.of(2024, 1, 9);
        final LocalDate future = LocalDate.of(2024, 1, 11);

        // when & then
        assertThat(isBetween(LocalDate.of(2024, 1, 8), past, future)).isFalse();
        assertThat(isBetween(LocalDate.of(2024, 1, 9), past, future)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 10), past, future)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 11), past, future)).isTrue();
        assertThat(isBetween(LocalDate.of(2024, 1, 12), past, future)).isFalse();
    }

    @Test
    public void isBeforeEquals_Test() {
        // given
        final LocalDate reference = LocalDate.of(2024, 1, 2);

        // when & then
        assertThat(isBeforeEquals(LocalDate.of(2024, 1, 1), reference)).isTrue();
        assertThat(isBeforeEquals(LocalDate.of(2024, 1, 2), reference)).isTrue();
        assertThat(isBeforeEquals(LocalDate.of(2024, 1, 3), reference)).isFalse();
    }

    @Test
    public void isAfterEquals_Test() {
        // given
        final LocalDate reference = LocalDate.of(2024, 1, 2);

        // when & then
        assertThat(isAfterEquals(LocalDate.of(2024, 1, 1), reference)).isFalse();
        assertThat(isAfterEquals(LocalDate.of(2024, 1, 2), reference)).isTrue();
        assertThat(isAfterEquals(LocalDate.of(2024, 1, 3), reference)).isTrue();
    }
}