package com.ebayil.meetingservice.controller.dto;

import com.ebayil.meetingservice.service.domain.Meeting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel
public class MeetingResponse
{
    @ApiModelProperty(required = true)
    private String message;

    public MeetingResponse(Meeting meeting) {
        this.message = "New Meeting was set for you! " + meeting;
    }
}

