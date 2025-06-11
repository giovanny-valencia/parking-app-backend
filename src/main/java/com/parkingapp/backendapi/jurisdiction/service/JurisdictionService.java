package com.parkingapp.backendapi.jurisdiction.service;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.mapper.JurisdictionMapper;
import com.parkingapp.backendapi.jurisdiction.record.JurisdictionData;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class JurisdictionService {

  private final JurisdictionRepository jurisdictionRepository;
  private final JurisdictionMapper jurisdictionMapper;

  /**
   * @return List of all supported jurisdictions
   */
  @Transactional(readOnly = true)
  public List<JurisdictionData> getSupportedJurisdictions() {
    return jurisdictionRepository.findAll().stream().map(jurisdictionMapper::toDto).toList();
  }

  /**
   * Used to retrieve a managed version of the Jurisdiction
   *
   * <p>Use cases: used cached jurisdictions to confirm supported jurisdiction, obtained from
   * officer jwt
   *
   * @param state Jurisdiction's state
   * @param city Jurisdiction's city
   * @return the databased managed Jurisdiction
   */
  @Transactional
  public Jurisdiction getManagedJurisdiction(State state, String city) {
    return jurisdictionRepository.findByStateAndCity(state, city);
  }
}
