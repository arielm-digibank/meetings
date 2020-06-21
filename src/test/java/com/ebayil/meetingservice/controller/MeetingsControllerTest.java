package com.ebayil.meetingservice.controller;

import com.ebayil.meetingservice.controller.dto.MeetingDto;
import com.ebayil.meetingservice.service.MeetingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetingsController.class)
public class MeetingsControllerTest {

    @MockBean
    private MeetingsService meetingsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testSetMeeting() throws Exception {
        Date thedate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-06-19");
        MeetingDto meetingDto = MeetingDto.builder().day(thedate).fromHour(8).fromMinute(0).toHour(9).toMinute(0).title("meeting").build();
        String content = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(meetingDto);

        RequestBuilder request = MockMvcRequestBuilders.post("/meetings/").content(content)
               .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().is(200))
                .andReturn();
        assertEquals("{\"message\":\"New Meeting was set for you! title: meeting, date:19/06/2020 08:00 - 09:00\"}", result.getResponse().getContentAsString());
    }

    @Test
    void testSetShortMeeting() throws Exception {
        Date thedate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-06-19");
        MeetingDto meetingDto = MeetingDto.builder().day(thedate).fromHour(8).fromMinute(0).toHour(8).toMinute(10).title("meeting").build();
        String content = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(meetingDto);

        RequestBuilder request = MockMvcRequestBuilders.post("/meetings/").content(content)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().is(400))
                .andReturn();
        assertEquals("{\"message\":\"Error in Meeting: Meeting cannot span less than 15 minutes\"}", result.getResponse().getContentAsString());
    }

    @Test
    void testSetLongMeeting() throws Exception {
        Date thedate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-06-19");
        MeetingDto meetingDto = MeetingDto.builder().day(thedate).fromHour(8).fromMinute(0).toHour(10).toMinute(10).title("meeting").build();
        String content = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(meetingDto);

        RequestBuilder request = MockMvcRequestBuilders.post("/meetings/").content(content)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().is(400))
                .andReturn();
        assertEquals("{\"message\":\"Error in Meeting: Meeting cannot span more than 2 hours\"}", result.getResponse().getContentAsString());
    }

    @Test
    void testSaturdayMeeting() throws Exception {
        Date thedate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-06-20");
        MeetingDto meetingDto = MeetingDto.builder().day(thedate).fromHour(8).fromMinute(0).toHour(9).toMinute(10).title("meeting").build();
        String content = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(meetingDto);

        RequestBuilder request = MockMvcRequestBuilders.post("/meetings/").content(content)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().is(400))
                .andReturn();
        assertEquals("{\"message\":\"Error in Meeting: Meeting cannot occur on Saturday\"}", result.getResponse().getContentAsString());
    }

}
