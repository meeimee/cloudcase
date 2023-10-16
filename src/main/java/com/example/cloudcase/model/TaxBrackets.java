package com.example.cloudcase.model;

import lombok.Data;

import java.math.BigDecimal;
@Data

public class TaxBrackets {
    private int currentFinancialYear;

    private BigDecimal taxFrom;

    private BigDecimal taxTo;

    private BigDecimal baseAmount;

    private BigDecimal additionalAmount;

    private BigDecimal additionalAmountLimit;


}
