package com.dexter.fyp.backend.dto;
import java.time.LocalDate;
import java.util.List;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class TimeSeriesGraphDto {

    private Status status;
    private String message;
    private List<XYData> data;

    @Data
    public static class XYData{
        private LocalDate date;
        private double percentage; 
    }
}
