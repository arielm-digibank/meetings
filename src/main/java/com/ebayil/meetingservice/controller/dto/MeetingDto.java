package com.ebayil.meetingservice.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ApiModel
public class MeetingDto {
    @ApiModelProperty(required = true)
    private String title;
    @ApiModelProperty(required = true)
    private Date day;
    @ApiModelProperty(required = true)
    private int fromHour;
    @ApiModelProperty(required = true)
    private int fromMinute;
    @ApiModelProperty(required = true)
    private int toHour;
    @ApiModelProperty(required = true)
    private int toMinute;
}
