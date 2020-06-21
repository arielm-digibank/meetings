package com.ebayil.meetingservice.service.impl;

import com.ebayil.meetingservice.exceptions.MeetingNotFoundException;
import com.ebayil.meetingservice.exceptions.MeetingsException;
import com.ebayil.meetingservice.service.domain.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MeetingsServiceImplTest {

    @Autowired
    MeetingsServiceLockableImpl service;

    final private static String TITLE = "Meeting";

    @BeforeEach
    void setUp () {
        service.clear();
    }

    @Test
    void testSetMeeting() {
        LocalDateTime from = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();
        service.setMeeting(meeting);
        assertEquals(service.getAllMeetings().get(0), meeting);
    }

    @Test()
    void testConnectedMeeting() {
        LocalDateTime from1 = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to1 = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting1 = Meeting.builder().title(TITLE).fromInclusive(from1).toExclusive(to1).build();
        service.setMeeting(meeting1);

        LocalDateTime from2 = LocalDateTime.of(2020, 6, 19, 8, 30);
        LocalDateTime to2 = LocalDateTime.of(2020, 6, 19, 9, 30);
        Meeting meeting2 = Meeting.builder().title(TITLE).fromInclusive(from2).toExclusive(to2).build();

        Exception exception = assertThrows(MeetingsException.class, () -> {
            service.setMeeting(meeting2);
        });
        String expectedMessage = "Meeting clashes with existing meeting";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test()
    void testLongDay() {
        LocalDateTime from1 = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to1 = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting1 = Meeting.builder().title(TITLE).fromInclusive(from1).toExclusive(to1).build();
        service.setMeeting(meeting1);

        LocalDateTime from2 = LocalDateTime.of(2020, 6, 19, 18, 00);
        LocalDateTime to2 = LocalDateTime.of(2020, 6, 19, 19, 00);
        Meeting meeting2 = Meeting.builder().title(TITLE).fromInclusive(from2).toExclusive(to2).build();

        Exception exception = assertThrows(MeetingsException.class, () -> {
            service.setMeeting(meeting2);
        });
        String expectedMessage = "Day is too long, more than 10 hours";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test()
    void testLongWeek() {
        for (int i=0; i < 5; i++)
            for (int j=0; j < 4; j++) {
                LocalDateTime from = LocalDateTime.of(2020, 6, 21+j, 8+2*i, 1);
                LocalDateTime to = LocalDateTime.of(2020, 6, 21+j, 10+2*i, 0);
                Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();
                service.setMeeting(meeting);
        }

        LocalDateTime from = LocalDateTime.of(2020, 6, 26, 8, 0);
        LocalDateTime to = LocalDateTime.of(2020, 6, 26, 10, 0);
        Meeting meeting2 = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();

        Exception exception = assertThrows(MeetingsException.class, () -> {
            service.setMeeting(meeting2);
        });
        String expectedMessage = "Cannot have more than 40 weekly hours of meetings";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testNextMeeting() {
        LocalDateTime from = LocalDateTime.of(2029, 8, 19, 8, 0);
        LocalDateTime to = LocalDateTime.of(2029, 8, 19, 9, 0);
        Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();

        LocalDateTime from2 = LocalDateTime.of(2024, 7, 19, 8, 0);
        LocalDateTime to2 = LocalDateTime.of(2024, 7, 19, 9, 0);
        Meeting meeting2 = Meeting.builder().title(TITLE).fromInclusive(from2).toExclusive(to2).build();

        service.setMeeting(meeting);
        service.setMeeting(meeting2);

        Meeting meetingResult = service.getNextMeeting();
        assertEquals(meeting2, meetingResult);
    }

    @Test
    void testNextMeetingNotFound() {
        LocalDateTime from = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();

        service.setMeeting(meeting);

        Exception exception = assertThrows(MeetingNotFoundException.class, () -> {
            service.getNextMeeting();
        });

        assertEquals(MeetingNotFoundException.class, exception.getClass());
    }

    @Test
    void testRemoveMeetingByFromTime() {
        LocalDateTime from = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();
        service.setMeeting(meeting);
        assertEquals(1, service.getAllMeetings().size());
        service.removeMeeting(from);
        assertEquals(0, service.getAllMeetings().size());
    }

    @Test
    void testRemoveMeetingByTitle() {
        LocalDateTime from = LocalDateTime.of(2020, 6, 19, 8, 0);
        LocalDateTime to = LocalDateTime.of(2020, 6, 19, 9, 0);
        Meeting meeting = Meeting.builder().title(TITLE).fromInclusive(from).toExclusive(to).build();
        service.setMeeting(meeting);

        LocalDateTime from2 = LocalDateTime.of(2020, 6, 21, 10, 0);
        LocalDateTime to2 = LocalDateTime.of(2020, 6, 21, 11, 0);
        Meeting meeting2 = Meeting.builder().title(TITLE).fromInclusive(from2).toExclusive(to2).build();
        service.setMeeting(meeting2);

        LocalDateTime from3 = LocalDateTime.of(2020, 6, 19, 10, 0);
        LocalDateTime to3 = LocalDateTime.of(2020, 6, 19, 11, 0);
        Meeting meeting3 = Meeting.builder().title("Another title").fromInclusive(from3).toExclusive(to3).build();
        service.setMeeting(meeting3);

        assertEquals(3, service.getAllMeetings().size());
        service.removeMeetings(TITLE);
        assertEquals(1, service.getAllMeetings().size());
    }
}
