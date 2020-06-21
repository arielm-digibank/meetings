package com.ebayil.meetingservice.controller;

import com.ebayil.meetingservice.controller.dto.FromTimeDto;
import com.ebayil.meetingservice.controller.dto.MeetingDto;
import com.ebayil.meetingservice.controller.dto.MeetingErrorResponse;
import com.ebayil.meetingservice.controller.dto.MeetingResponse;
import com.ebayil.meetingservice.exceptions.MeetingNotFoundException;
import com.ebayil.meetingservice.exceptions.MeetingsException;
import com.ebayil.meetingservice.service.MeetingsService;
import com.ebayil.meetingservice.service.domain.Meeting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "meetings", produces = {APPLICATION_JSON_VALUE})
@Api(value = "REST API Meeting API")
public class MeetingsController {

    @Autowired
    MeetingsService meetingsServiceLockableImpl;

    @RequestMapping(value = "/", method = RequestMethod.POST ,produces = {MediaType.APPLICATION_JSON_VALUE},consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "An API to set new meeting", notes = "An API to set new meeting", response = MeetingResponse.class)
    public MeetingResponse setNewMeeting(@RequestBody MeetingDto meetingDto) {
        Meeting newMeeting = Meeting.fromMeetingDto(meetingDto);
        validate(newMeeting);
        meetingsServiceLockableImpl.setMeeting(newMeeting);
        return new MeetingResponse(newMeeting);
    }

    @RequestMapping(value = "/delete/fromtime", method = RequestMethod.DELETE ,produces = {MediaType.APPLICATION_JSON_VALUE},consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiOperation(httpMethod = "DELETE", value = "An API to delete a meeting by start time", notes = "An API to delete a meeting by start time", response = MeetingResponse.class)
    public MeetingResponse removeMeetingByFromTime(@RequestBody FromTimeDto fromTimeDto) {
        LocalDateTime fromTime = Meeting.convertToLocalDateTime(fromTimeDto.getDay(), fromTimeDto.getFromHour(), fromTimeDto.getFromMinute());
        String returnVal = meetingsServiceLockableImpl.removeMeeting(fromTime);
        return new MeetingResponse("Removed:" + returnVal);
    }

    @RequestMapping(value = "/delete/bytitle", method = RequestMethod.DELETE ,produces = {MediaType.APPLICATION_JSON_VALUE},consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @ApiOperation(httpMethod = "DELETE", value = "An API to delete meetings by title", notes = "An API to delete meetings by title", response = MeetingResponse.class)
    public MeetingResponse removeMeetingsByTitle(@RequestBody String title) {
        String returnVal = meetingsServiceLockableImpl.removeMeetings(title);
        if (returnVal.endsWith(",")) returnVal = returnVal.substring(0, returnVal.length()-1);
        return new MeetingResponse("Removed:" + returnVal);
    }

    @GetMapping("/")
    @ApiOperation(httpMethod = "GET", value = "An API to print all meetings", notes = "An API to print all meetings", response = MeetingResponse.class)
    public List<Meeting> getMeetings() {
        return meetingsServiceLockableImpl.getAllMeetings();
    }

    @GetMapping("/next")
    @ApiOperation(httpMethod = "GET", value = "An API to print next meeting", notes = "An API to print next meeting", response = MeetingResponse.class)
    public Meeting getNextMeeting() {
        return meetingsServiceLockableImpl.getNextMeeting();
    }


    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MeetingNotFoundException.class)
    @ResponseBody
    public MeetingErrorResponse handleNotFoundError(MeetingNotFoundException ex) {
        return new MeetingErrorResponse("Meeting Not Found !!");
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MeetingsException.class)
    @ResponseBody
    public MeetingErrorResponse handleLogicalErrors(MeetingsException ex) {
        return new MeetingErrorResponse("Error in Meeting: " + ex.getMessage());
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeException.class)
    @ResponseBody
    public MeetingErrorResponse handleMalformedDates(DateTimeException ex) {
        return new MeetingErrorResponse("Error In Dates: " + ex.getMessage());
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public MeetingErrorResponse handleMalformedRequest(Exception ex) {
        return new MeetingErrorResponse("Error In Request: ");
    }

    private void validate(Meeting meeting) {
        if (meeting.getFromInclusive().isAfter(meeting.getToExclusive())) throw new MeetingsException("To should be after From");

        int lengthInMin = meeting.getMeetingLengthinMinutes();

        if (lengthInMin > 120) throw new MeetingsException("Meeting cannot span more than 2 hours");

        if (lengthInMin < 15) throw new MeetingsException("Meeting cannot span less than 15 minutes");

        if (meeting.getFromInclusive().getDayOfWeek() == DayOfWeek.SATURDAY) throw new MeetingsException("Meeting cannot occur on Saturday");
    }

}
