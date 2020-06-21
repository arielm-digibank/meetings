package com.ebayil.meetingservice.service.impl;

import com.ebayil.meetingservice.data.MeetingsManager;
import com.ebayil.meetingservice.exceptions.MeetingNotFoundException;
import com.ebayil.meetingservice.exceptions.MeetingsException;
import com.ebayil.meetingservice.service.MeetingsService;
import com.ebayil.meetingservice.service.domain.Meeting;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingsServiceImpl implements MeetingsService {

    @Autowired
    MeetingsManager meetingManager;

    @Override
    public void setMeeting(Meeting meeting) {
        validate(meeting);
        meetingManager.setNewMeeting(meeting);
    }

    @Override
    public String removeMeeting(LocalDateTime fromTime) {
        String returnVal =  meetingManager.removeMeeting(fromTime);
        if (StringUtils.isEmpty(returnVal)) throw new MeetingNotFoundException();
        return returnVal;
    }

    @Override
    public String removeMeetings(String meetingTitle) {
        String returnVal = meetingManager.removeMeetings(meetingTitle);
        if (StringUtils.isEmpty(returnVal)) throw new MeetingNotFoundException();
        if (returnVal.endsWith(",")) returnVal = returnVal.substring(0, returnVal.length()-1);
        return returnVal;
    }

    @Override
    public Meeting getNextMeeting() {
        return meetingManager.getNextMeeting().orElseThrow(() -> new MeetingNotFoundException());
    }

    @Override
    public Meeting getNextMeeting(LocalDateTime afterThis) {
        return meetingManager.getNextMeeting(afterThis).orElseThrow(() -> new MeetingNotFoundException());
    }

    @Override
    public List<Meeting> getAllMeetings() {
        return meetingManager.getAllMeetings();
    }

    @VisibleForTesting
    void clear() {
        meetingManager.clear();
    }

    private void validate(Meeting meeting) {
        int lengthInMin = meeting.getMeetingLengthinMinutes();

        LocalDate localDate = meeting.getFromInclusive().toLocalDate();

        if (meetingManager.totalMinutesOfWeeklyMeetings(localDate) + lengthInMin > 40*60) throw new MeetingsException("Cannot have more than 40 weekly hours of meetings");

        meetingManager.minMaxDailyMeetings(localDate).ifPresent(pair -> {
            LocalDateTime min = pair.getKey().isBefore(meeting.getFromInclusive()) ? pair.getKey() : meeting.getFromInclusive();
            LocalDateTime max = pair.getValue().isAfter(meeting.getToExclusive()) ? pair.getValue() : meeting.getToExclusive();
            if (min.until( max, ChronoUnit.MINUTES ) > 600L) throw new MeetingsException("Day is too long, more than 10 hours - start from:" + min + " until " + max);
        });

        Optional<Meeting> connectedMeeting = meetingManager.getConnected(meeting);
        if (connectedMeeting.isPresent()) {
            throw new MeetingsException("Meeting clashes with existing meeting: "+ connectedMeeting.get());
        }
    }
}
