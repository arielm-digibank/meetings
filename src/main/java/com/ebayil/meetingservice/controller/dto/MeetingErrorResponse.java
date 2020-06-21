package com.ebayil.meetingservice.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel
public class MeetingErrorResponse
{
    @ApiModelProperty(required = true)
    private String message;
}
