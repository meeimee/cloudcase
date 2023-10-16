package com.example.cloudcase.dao;

import com.example.cloudcase.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeDao extends CrudRepository<Employee, Long> {

}
