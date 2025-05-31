package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.report.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByPlateStateAndPlateNumber(State plateState, String plateNumber);
}