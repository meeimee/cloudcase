package com.example.cloudcase.service;

import com.example.cloudcase.dao.EmployeeDao;
import com.example.cloudcase.dao.SalaryDetailsDao;
import com.example.cloudcase.dao.SuperGuaranteedContributionDao;
import com.example.cloudcase.dao.TaxBracketsDao;
import com.example.cloudcase.model.Employee;
import com.example.cloudcase.model.SalaryDetails;
import com.example.cloudcase.model.SuperGuaranteedContribution;
import com.example.cloudcase.model.TaxBrackets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PayDetailsTests {

    @Mock
    private SalaryDetailsDao salaryDetailsDao ;
    @Mock
    private TaxBracketsDao taxBracketsDao ;
    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private SuperGuaranteedContributionDao superGuaranteedContributionDao;
    @InjectMocks
    private PayDetails payDetails;

    @Test
    public void calculateAnnualTax_whenEmployeeIdIsValid_shouldSuccessfullyCalculate(){
        long employeeId = 1;
        int currentYear = LocalDate.now().getYear();

        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryAmount(new BigDecimal(120_000));

        TaxBrackets taxBrackets = new TaxBrackets();
        taxBrackets.setBaseAmount(new BigDecimal(20_797));
        taxBrackets.setAdditionalAmount(new BigDecimal(37));
        taxBrackets.setAdditionalAmountLimit(new BigDecimal(90_000));

        when(salaryDetailsDao.findByEmployeeIdByYear(currentYear, employeeId)).thenReturn(salaryDetails);
        when(taxBracketsDao.findBySalary(salaryDetails.getSalaryAmount())).thenReturn(taxBrackets);
        when(employeeDao.findById(employeeId)).thenReturn(Optional.of(new Employee()));

        BigDecimal annualTax = payDetails.calculateAnnualTax(employeeId, currentYear);
        assertEquals(new BigDecimal("31897.00"), annualTax);
    }

    @Test
    public void calculateAnnualTax_whenEmployeeSalary20000_shouldReturnCorrectTax(){
        long employeeId = 1;
        int currentYear = LocalDate.now().getYear();


        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryAmount(new BigDecimal(20_000));

        TaxBrackets taxBrackets = new TaxBrackets();
        taxBrackets.setBaseAmount(new BigDecimal("0.00"));
        taxBrackets.setAdditionalAmount(new BigDecimal(19));
        taxBrackets.setAdditionalAmountLimit(new BigDecimal(18200));

        when(salaryDetailsDao.findByEmployeeIdByYear(currentYear, employeeId)).thenReturn(salaryDetails);
        when(taxBracketsDao.findBySalary(salaryDetails.getSalaryAmount())).thenReturn(taxBrackets);
        when(employeeDao.findById(employeeId)).thenReturn(Optional.of(new Employee()));

        BigDecimal annualTax = payDetails.calculateAnnualTax(employeeId, currentYear);
        BigDecimal expectedTax = new BigDecimal("342.00");
        assertEquals( expectedTax, annualTax);
    }

    @Test
    public void calculateAnnualTax_whenEmployeeSalaryIsNotTaxable_shouldReturnZero(){
        long employeeId = 1;
        int currentYear = LocalDate.now().getYear();

        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryAmount(new BigDecimal(18200));

        TaxBrackets taxBrackets = new TaxBrackets();
        taxBrackets.setBaseAmount(new BigDecimal("0.00"));
        taxBrackets.setAdditionalAmount(new BigDecimal("0.00"));
        taxBrackets.setAdditionalAmountLimit(new BigDecimal("0.00"));

        when(salaryDetailsDao.findByEmployeeIdByYear(currentYear, employeeId)).thenReturn(salaryDetails);
        when(taxBracketsDao.findBySalary(salaryDetails.getSalaryAmount())).thenReturn(taxBrackets);
        when(employeeDao.findById(employeeId)).thenReturn(Optional.of(new Employee()));

        BigDecimal annualTax = payDetails.calculateAnnualTax(employeeId, currentYear);
        BigDecimal expectedTax = new BigDecimal("0.00");
        assertEquals( expectedTax, annualTax);
    }

    @Test(expected = RuntimeException.class)
    public void calculateAnnualTax_whenEmployeeIdIsNotValid_shouldReturnException(){
        long employeeId = -1;
        int currentYear = LocalDate.now().getYear();

        payDetails.calculateAnnualTax(employeeId, currentYear);
    }

    @Test(expected = RuntimeException.class)
    public void calculateAnnualTax_whenEmployeeIdDoesNotExists_shouldReturnException(){
        long employeeId = 99;
        int currentYear = LocalDate.now().getYear();
        payDetails.calculateAnnualTax(employeeId, currentYear);
    }

    @Test(expected = RuntimeException.class)
    public void calculateSuper_whenEmployeeIdIsNotValid_shouldReturnException(){
        long employeeId = -1;
        payDetails.calculateSuper(employeeId);
    }

    @Test(expected = RuntimeException.class)
    public void calculateSuper_whenEmployeeIdDoesNotExists_shouldReturnException(){
        long employeeId = 99;
        payDetails.calculateSuper(employeeId);
    }

    @Test
    public void calculateSuper_whenEmployeeUses11Percent_shouldSuccessfullyCalculate(){
        long employeeId = 12;

        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryAmount(new BigDecimal(120_000));

        Employee employee = new Employee();

        SuperGuaranteedContribution sgc = new SuperGuaranteedContribution();
        sgc.setSuperGuaranteedContributionPercentage(11);

        when(employeeDao.findById(employeeId)).thenReturn(Optional.of(employee));
        when(salaryDetailsDao.findByEmployeeIdByYear(LocalDate.now().getYear(), employeeId)).thenReturn(salaryDetails);
        when(superGuaranteedContributionDao.findByMaxEffectiveDate()).thenReturn(Optional.of(sgc));


        BigDecimal totalSuper = payDetails.calculateSuper(employeeId);


        assertEquals(new BigDecimal("13200.00"), totalSuper);
    }

    @Test
    public void calculateSuper_whenEmployeeUsesCustomSuper_shouldSuccessfullyCalculate(){
        long employeeId = 12;

        SalaryDetails salaryDetails = new SalaryDetails();
        salaryDetails.setSalaryAmount(new BigDecimal(120_000));

        Employee employee = new Employee();

        SuperGuaranteedContribution sgc = new SuperGuaranteedContribution();
        sgc.setSuperGuaranteedContributionPercentage(15);

        when(employeeDao.findById(employeeId)).thenReturn(Optional.of(employee));
        when(salaryDetailsDao.findByEmployeeIdByYear(LocalDate.now().getYear(), employeeId)).thenReturn(salaryDetails);
        when(superGuaranteedContributionDao.findByMaxEffectiveDate()).thenReturn(Optional.of(sgc));


        BigDecimal totalSuper = payDetails.calculateSuper(employeeId);

        assertEquals(new BigDecimal("18000.00"), totalSuper);
    }
}
