package com.parkingapp.backendapi.report.service;

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

  @Transactional(readOnly = true)
  public List<JurisdictionData> getSupportedJurisdictions() {
    return jurisdictionRepository.findAll().stream().map(jurisdictionMapper::toDto).toList();
  }
}
