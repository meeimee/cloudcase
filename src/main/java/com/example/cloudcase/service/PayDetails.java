package com.example.cloudcase.service;

import com.example.cloudcase.dao.EmployeeDao;
import com.example.cloudcase.dao.SalaryDetailsDao;
import com.example.cloudcase.dao.SuperGuaranteedContributionDao;
import com.example.cloudcase.dao.TaxBracketsDao;
import com.example.cloudcase.model.Employee;
import com.example.cloudcase.model.SalaryDetails;
import com.example.cloudcase.model.SuperGuaranteedContribution;
import com.example.cloudcase.model.TaxBrackets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

public class PayDetails {

    private final SalaryDetailsDao salaryDetailsDao;
    private final TaxBracketsDao taxBracketsDao;
    private final EmployeeDao employeeDao;

    private final SuperGuaranteedContributionDao superGuaranteedContributionDao;


    public PayDetails(SalaryDetailsDao salaryDetailsDao,
                      TaxBracketsDao taxBracketsDao,
                      EmployeeDao employeeDao,
                      SuperGuaranteedContributionDao superGuaranteedContributionDao) {
        this.salaryDetailsDao = salaryDetailsDao;
        this.taxBracketsDao = taxBracketsDao;
        this.employeeDao = employeeDao;
        this.superGuaranteedContributionDao = superGuaranteedContributionDao;
    }

    public BigDecimal calculateAnnualTax(long employeeId, int currentYear) {
        if (employeeId < 0) {
            throw new RuntimeException("Employee ID is not valid");
        }

        Optional<Employee> employee = employeeDao.findById(employeeId);

        if (employee.isEmpty()) {
            throw new RuntimeException("Employee does not exists");
        }


        SalaryDetails salaryDetails = salaryDetailsDao.findByEmployeeIdByYear(currentYear, employeeId);
        TaxBrackets taxBrackets = taxBracketsDao.findBySalary(salaryDetails.getSalaryAmount());

        return calculateAnnualTaxAmount(taxBrackets, salaryDetails);
    }

    /*
     * Calculates the annual tax amount based on salary and tax bracket.
     * Formula = ((salary - additionalAmountLimit) * (additionalAmount/100))) + baseAmount
     * Assumption = residency status is resident for full year
     * Sample = ((120,000 - 90,000) * (37/100))) + 20797 = 31,897
     *
     * */
    private BigDecimal calculateAnnualTaxAmount(TaxBrackets taxBrackets, SalaryDetails salaryDetails) {

        BigDecimal baseAmount = taxBrackets.getBaseAmount();
        BigDecimal salaryAmount = salaryDetails.getSalaryAmount();
        BigDecimal additionalAmountLimit = taxBrackets.getAdditionalAmountLimit();
        BigDecimal additionalAmount = taxBrackets.getAdditionalAmount();

        BigDecimal adjustedAmount = salaryAmount.subtract(additionalAmountLimit);
        BigDecimal additionalAmountPercentage = additionalAmount.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal additionalTax = adjustedAmount.multiply(additionalAmountPercentage);

        BigDecimal annualTaxAmount = baseAmount.add(additionalTax).setScale(2, RoundingMode.HALF_UP);

        return annualTaxAmount;
    }

    public BigDecimal calculateSuper(long employeeId) {
        if (employeeId < 0) {
            throw new RuntimeException("Employee ID is not valid");
        }

        Optional<Employee> employee = employeeDao.findById(employeeId);

        if (employee.isEmpty()) {
            throw new RuntimeException("Employee does not exists");
        }

        Optional<SuperGuaranteedContribution> superGuaranteedContribution = superGuaranteedContributionDao.findByMaxEffectiveDate();

        if(superGuaranteedContribution.isEmpty()){
            throw new RuntimeException("No SuperGuaranteedContribution available.");
        }

        SuperGuaranteedContribution sgc = superGuaranteedContribution.get();

        SalaryDetails salaryDetails = salaryDetailsDao.findByEmployeeIdByYear(LocalDate.now().getYear(), employeeId);

        return salaryDetails.getSalaryAmount().
                multiply(new BigDecimal(sgc.getSuperGuaranteedContributionPercentage())).
                divide(new BigDecimal("100"),2, RoundingMode.HALF_UP);
    }
}
