package com.ebayil.meetingservice.data;

import com.ebayil.meetingservice.service.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

public interface MeetingsManager {
    void setNewMeeting(Meeting meeting);
    List<Meeting> getAllMeetings();
    Optional<Meeting> getNextMeeting();
    Optional<Meeting> getNextMeeting(LocalDateTime afterThis);
    String removeMeeting(LocalDateTime from);
    String removeMeetings(String title);
    Optional<AbstractMap.SimpleEntry<LocalDateTime, LocalDateTime>> minMaxDailyMeetings(LocalDate theDate);
    int totalMinutesOfWeeklyMeetings(LocalDate theDate);
    Optional<Meeting> getConnected(Meeting meeting);
    void clear();
}
