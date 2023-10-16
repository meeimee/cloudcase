package com.example.cloudcase.dao;

import com.example.cloudcase.model.SalaryDetails;

public interface SalaryDetailsDao {
    SalaryDetails findByEmployeeIdByYear(int year, long employeeId);
}
