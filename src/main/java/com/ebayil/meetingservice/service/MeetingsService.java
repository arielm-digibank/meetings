package com.ebayil.meetingservice.service;

import com.ebayil.meetingservice.service.domain.Meeting;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingsService {
    void setMeeting(Meeting meeting);
    String removeMeeting(LocalDateTime fromTime);
    String removeMeetings(String meetingTitle);
    Meeting getNextMeeting();
    Meeting getNextMeeting(LocalDateTime afterThis);
    List<Meeting> getAllMeetings();
}
