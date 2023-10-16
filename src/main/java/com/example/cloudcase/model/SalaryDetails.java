package com.example.cloudcase.model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SalaryDetails {

    private int currentYear;

    private BigDecimal salaryAmount;

    private String salaryType;
}
