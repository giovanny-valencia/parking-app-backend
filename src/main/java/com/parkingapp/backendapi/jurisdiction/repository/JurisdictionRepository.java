package com.parkingapp.backendapi.jurisdiction.repository;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  JurisdictionRepository extends JpaRepository<Jurisdiction, Long> {
    Jurisdiction findByStateAndCity(State state, String city);
}
