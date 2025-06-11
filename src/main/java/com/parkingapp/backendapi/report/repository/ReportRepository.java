package com.parkingapp.backendapi.report.repository;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.report.entity.Report;
import com.parkingapp.backendapi.report.entity.Status;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  // when checking for the duplicate vehicle in DB, the vehicle is an unmanaged entity
  // that means passing the Vehicle entity won't work because the ID is null
  // pass the state and plate number specifically
  boolean existsByVehiclePlateStateAndVehiclePlateNumberAndStatusIn(
      State plateState, String plateNumber, Collection<Status> statuses);

  //  Officer polling call to fetch active reports in their jurisdiction
  List<Report> findByAddress_JurisdictionAndStatusInAndCreatedOnAfter(
      Jurisdiction jurisdiction, Collection<Status> statuses, Instant createdOnCutoff);
}
