package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  ReportRepository extends JpaRepository<Report, Long> {
}
