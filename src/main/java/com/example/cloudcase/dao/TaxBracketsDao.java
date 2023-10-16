package com.example.cloudcase.dao;

import com.example.cloudcase.model.TaxBrackets;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TaxBracketsDao {

    @Query("SELECT t FROM TaxBrackets t WHERE t.taxFrom <= :salary AND t.taxTo >= :salary")
    TaxBrackets findBySalary(BigDecimal salary);
}
