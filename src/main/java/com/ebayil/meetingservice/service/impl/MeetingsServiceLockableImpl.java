package com.ebayil.meetingservice.service.impl;

import com.ebayil.meetingservice.service.MeetingsService;
import com.ebayil.meetingservice.service.domain.Meeting;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class MeetingsServiceLockableImpl implements MeetingsService {

    @Autowired
    MeetingsServiceImpl meetingsServiceImpl;

    final private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final private Lock writeLock = lock.writeLock();
    final private Lock readLock = lock.readLock();

    @Override
    public void setMeeting(Meeting meeting) {
        try {
            writeLock.lock();
            meetingsServiceImpl.setMeeting(meeting);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String removeMeeting(LocalDateTime fromTime) {
        try {
            writeLock.lock();
            return meetingsServiceImpl.removeMeeting(fromTime);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String removeMeetings(String meetingTitle) {
        try {
            writeLock.lock();
            return meetingsServiceImpl.removeMeetings(meetingTitle);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Meeting getNextMeeting() {
        try {
            readLock.lock();
            return meetingsServiceImpl.getNextMeeting();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Meeting getNextMeeting(LocalDateTime afterThis) {
        try {
            readLock.lock();
            return meetingsServiceImpl.getNextMeeting(afterThis);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Meeting> getAllMeetings() {
        try {
            readLock.lock();
            return meetingsServiceImpl.getAllMeetings();
        } finally {
            readLock.unlock();
        }
    }

    @VisibleForTesting
    void clear() {
        try {
            writeLock.lock();
            meetingsServiceImpl.clear();
        } finally {
            writeLock.unlock();
        }
    }
}
