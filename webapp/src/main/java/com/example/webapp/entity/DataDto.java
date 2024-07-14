package com.example.webapp.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataDto {

    private String type;
    private String status;
    private String message;
    private Data data;

}
