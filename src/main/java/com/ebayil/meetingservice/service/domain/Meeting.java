package com.ebayil.meetingservice.service.domain;

import com.ebayil.meetingservice.controller.dto.MeetingDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Meeting {
    private String title;
    private LocalDateTime fromInclusive;
    private LocalDateTime toExclusive;

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static Meeting fromMeetingDto(MeetingDto meetingDto) {
        return Meeting.builder()
                .title(meetingDto.getTitle())
                .fromInclusive(convertToLocalDateTime(meetingDto.getDay(), meetingDto.getFromHour(), meetingDto.getFromMinute()))
                .toExclusive(convertToLocalDateTime(meetingDto.getDay(), meetingDto.getToHour(), meetingDto.getToMinute()))
        .build();
    }

    public static LocalDateTime convertToLocalDateTime(Date theDate, int hour, int minute) {
        LocalDate localDate = theDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate.atTime(hour, minute);
    }

    public int getMeetingLengthinMinutes() {
        return (int) fromInclusive.until( toExclusive, ChronoUnit.MINUTES );
    }

    @Override
    public String toString() {
        return "title: " + title +
                ", date:" + fromInclusive.format(dateFormatter) + " " +
                fromInclusive.format(timeFormatter) + " - " + toExclusive.format(timeFormatter);
    }
}
