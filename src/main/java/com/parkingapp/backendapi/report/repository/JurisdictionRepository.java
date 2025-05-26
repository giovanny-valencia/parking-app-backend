package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.report.entity.Jurisdiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  JurisdictionRepository extends JpaRepository<Jurisdiction, Long> {
}
