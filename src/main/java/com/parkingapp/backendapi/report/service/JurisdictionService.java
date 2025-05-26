package com.parkingapp.backendapi.report.service;

import com.parkingapp.backendapi.report.dto.JurisdictionResponse;
import com.parkingapp.backendapi.report.entity.Jurisdiction;
import com.parkingapp.backendapi.report.mapper.JurisdictionMapper;
import com.parkingapp.backendapi.report.repository.JurisdictionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class JurisdictionService {

    private final JurisdictionRepository jurisdictionRepository;
    private final JurisdictionMapper jurisdictionMapper;

    @Transactional(readOnly = true)
    public List<JurisdictionResponse> getSupportedJurisdictions(){
        return jurisdictionRepository
                .findAll()
                .stream()
                .map(jurisdictionMapper::toDto)
                .toList();
    }
}
