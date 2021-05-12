package com.example.sem6.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatingSchedule {
    private Long id;
    private Long transactionId;
    private Date startBookingTime;
    private Date endBookingTime;
    private List<UserDatingSchedule> userDatingSchedules;
    private Transaction transaction;
    private int status;
    private Date createdAt;
    private Date updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserDatingSchedule {
        private User user;
    }
}
