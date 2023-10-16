package com.example.cloudcase.model;

import lombok.Data;

import java.util.Date;

@Data
public class SuperGuaranteedContribution {

    private Long id;

    private Integer superGuaranteedContributionPercentage;

    private Date effectiveDate;
}
