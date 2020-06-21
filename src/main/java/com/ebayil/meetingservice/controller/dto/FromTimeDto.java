package com.ebayil.meetingservice.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.Date;

@Getter
@ApiModel
public class FromTimeDto {
    @ApiModelProperty(required = true)
    private Date day;
    @ApiModelProperty(required = true)
    private int fromHour;
    @ApiModelProperty(required = true)
    private int fromMinute;
}
