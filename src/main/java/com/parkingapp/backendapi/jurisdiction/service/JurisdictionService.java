package com.parkingapp.backendapi.jurisdiction.service;

import com.parkingapp.backendapi.jurisdiction.dto.UserLocationDto;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JurisdictionService {

  private JurisdictionCacheService jurisdictionCacheService;

  public Optional<Jurisdiction> isLocationSupported(UserLocationDto userLocationDto) {

    return jurisdictionCacheService.findJurisdictionByCoordinates(
        userLocationDto.longitude().doubleValue(), userLocationDto.latitude().doubleValue());
  }
}
