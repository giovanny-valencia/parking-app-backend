package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.Status;
import com.parkingapp.backendapi.report.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface  ReportRepository extends JpaRepository<Report, Long> {

    // when checking for the duplicate vehicle in DB, the vehicle is an unmanaged entity
    // that means passing the Vehicle entity won't work because the ID is null
    // pass the state and plate number specifically
    boolean existsByVehiclePlateStateAndVehiclePlateNumberAndStatusIn(
            State plateState,
            String plateNumber,
            Collection<Status> statuses
    );
}
