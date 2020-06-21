package com.ebayil.meetingservice.data.impl;

import com.ebayil.meetingservice.data.MeetingsManager;
import com.ebayil.meetingservice.service.domain.Meeting;
import com.google.common.collect.*;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeetingsManagerImpl implements MeetingsManager {

    private RangeMap<LocalDateTime, String> meetingsRangeMap = TreeRangeMap.create();
    private Multimap<String, Range> meetingsByTitle = ArrayListMultimap.create();

    @Override
    public void setNewMeeting(Meeting meeting) {
        Range range = createRange(meeting);
        innerSetMeeting(range, meeting.getTitle());
    }

    @Override
    public List<Meeting> getAllMeetings() {
        return meetingsRangeMap.asMapOfRanges().entrySet().stream()
                .map(this::convertFromEntry)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Meeting> getConnected(Meeting meeting) {
        Range<LocalDateTime> range = createRange(meeting);
        Optional<Map.Entry<Range<LocalDateTime>, String>> connectedMeeting = meetingsRangeMap.asMapOfRanges().entrySet().stream().filter(e -> e.getKey().isConnected(range)).findFirst();
        return connectedMeeting.isPresent() ? Optional.of(convertFromEntry(connectedMeeting.get())) : Optional.empty();
    }

    @Override
    public int totalMinutesOfWeeklyMeetings(LocalDate theDate) {
        LocalDateTime startOfWeek = theDate.with(DayOfWeek.SUNDAY).atStartOfDay();
        LocalDateTime endOfWeek = theDate.with(DayOfWeek.SATURDAY).atStartOfDay();
        if (startOfWeek.isAfter(endOfWeek)) startOfWeek = startOfWeek.minus(7, ChronoUnit.DAYS);
        Range<LocalDateTime> spanRange = Range.closedOpen(startOfWeek, endOfWeek);

        return meetingsRangeMap.subRangeMap(spanRange).asMapOfRanges()
                .keySet().stream()
                .mapToInt(r -> (int) r.lowerEndpoint().until( r.upperEndpoint(), ChronoUnit.MINUTES ))
                .sum();
    }

    @Override
    public Optional<AbstractMap.SimpleEntry<LocalDateTime, LocalDateTime>> minMaxDailyMeetings(LocalDate theDate) {
        LocalDateTime startOfDay = theDate.atStartOfDay();
        LocalDateTime endOfDay = theDate.atTime(23, 59);
        Range<LocalDateTime> spanRange = Range.closedOpen(startOfDay, endOfDay);

        Map<Range<LocalDateTime>, String> dayMeetingsAsc = meetingsRangeMap.subRangeMap(spanRange).asMapOfRanges();
        Map<Range<LocalDateTime>, String> dayMeetingsDesc = meetingsRangeMap.subRangeMap(spanRange).asDescendingMapOfRanges();

        Optional<Range<LocalDateTime>> startDayRange = dayMeetingsAsc.keySet().stream().findFirst();
        Optional<Range<LocalDateTime>> endDayRange = dayMeetingsDesc.keySet().stream().findFirst();

        if (startDayRange.isPresent()) {
            return Optional.of(new AbstractMap.SimpleEntry<>(startDayRange.get().lowerEndpoint(), endDayRange.get().upperEndpoint()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String removeMeetings(String title) {
        StringBuilder s = new StringBuilder();
        Collection<Range> ranges = new ArrayList(meetingsByTitle.get(title));
        ranges.stream().forEach(r -> {
            meetingsRangeMap.remove(r);
            meetingsByTitle.remove(title, r);
            s.append(r).append(",");
        });
        return s.toString();
    }

    @Override
    public String removeMeeting(LocalDateTime from) {
        StringBuilder s = new StringBuilder();
        Optional<Map.Entry<Range<LocalDateTime>, String>> toDelete =  meetingsRangeMap.asMapOfRanges().entrySet().stream().filter(e -> e.getKey().lowerEndpoint().equals(from)).findFirst();
        toDelete.ifPresent(e -> {
            meetingsRangeMap.remove(e.getKey());
            meetingsByTitle.remove(e.getValue(), e.getKey());
            s.append(e.getValue());
        }) ;
        return s.toString();
    }

    @Override
    public Optional<Meeting> getNextMeeting() {
        return getNextMeeting(LocalDateTime.now());
    }

    @Override
    public Optional<Meeting> getNextMeeting(LocalDateTime afterThis) {
        RangeMap<LocalDateTime, String> subRangeMap = meetingsRangeMap.subRangeMap(Range.greaterThan(afterThis));
        if (subRangeMap.asMapOfRanges().size() == 0) return Optional.empty();

        return Optional.of(convertFromEntry(subRangeMap.asMapOfRanges().entrySet().iterator().next()));
    }

    @Override
    public void clear() {
        meetingsRangeMap.clear();
        meetingsByTitle.clear();
    }


    private Meeting convertFromEntry(Map.Entry<Range<LocalDateTime>, String> e) {
        return Meeting.builder().title(e.getValue()).fromInclusive(e.getKey().lowerEndpoint()).toExclusive(e.getKey().upperEndpoint()).build();
    }

    private void innerSetMeeting(Range range,  String title) {
        meetingsRangeMap.put(range, title);
        meetingsByTitle.put(title, range);
    }

    private Range createRange(Meeting meeting) {
        Range<LocalDateTime> range = Range.closedOpen(meeting.getFromInclusive(), meeting.getToExclusive());
        return range;
    }
}
