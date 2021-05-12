package com.example.sem6.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private long id;
    private User user;
    private DatingSchedule datingSchedule;
    private double amount;
    private double rating;
    private String ratingComment;
    private int type;
    private int status;
    private Date createdAt;
    private Date updatedAt;
}
