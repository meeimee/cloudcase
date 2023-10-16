package com.example.cloudcase.dao;

import com.example.cloudcase.model.SuperGuaranteedContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SuperGuaranteedContributionDao extends JpaRepository<SuperGuaranteedContribution, Long> {

    @Query(value = "SELECT TOP 1 * FROM super_guaranteed_contribution s order by s.effective_date desc ", nativeQuery = true)
    Optional<SuperGuaranteedContribution> findByMaxEffectiveDate();
}
