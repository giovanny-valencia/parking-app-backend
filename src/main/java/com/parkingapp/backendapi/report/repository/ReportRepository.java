package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface  ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByVehicleAndStatusIn(Vehicle vehicle, Collection<Status> statuses);
}
