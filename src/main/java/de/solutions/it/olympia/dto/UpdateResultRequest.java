package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class UpdateResultRequest {
    private String value;
    private Integer rank;
}